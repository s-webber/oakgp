/*
 * Copyright 2015 S. Webber
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oakgp.function;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.node.Node;

/** Represents an operation. */
public interface Function {
   /**
    * Returns the result of applying this operation to the specified {@code Arguments} and {@code Assignments}.
    *
    * @param arguments
    *           represents the arguments to apply to the operation
    * @param assignments
    *           represents values assigned to variables belonging to {@code arguments}
    * @return the result of applying this operation to the {@code arguments} and {@code assignments}
    */
   Object evaluate(Arguments arguments, Assignments assignments);

   Signature getSignature();

   default Node simplify(Arguments arguments) {
      return null;
   }

   default String getDisplayName() {
      String className = getClass().getName();
      int packagePos = className.lastIndexOf('.');
      String lowerCaseNameMinusPackage = className.substring(packagePos + 1).toLowerCase();
      if (lowerCaseNameMinusPackage.startsWith("is") && getSignature().getReturnType() == Type.booleanType()) {
         return lowerCaseNameMinusPackage.substring(2) + "?";
      } else {
         return lowerCaseNameMinusPackage;
      }
   }
}
