/*
 * Copyright 2019 S. Webber
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
package org.oakgp.function.compare;

import static org.oakgp.type.CommonTypes.comparableType;

import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.AutomaticallyDefinedFunctions;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;

/** Compares two arguments and returns the maximum. */
public final class Max implements Function {
   private static final Max SINGLETON = new Max();

   public static Max getSingleton() {
      return SINGLETON;
   }

   private final Signature signature;

   private Max() {
      Type type = Types.generic("ElementType", comparableType());
      this.signature = Signature.createSignature(type, type, type);
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   @Override
   public Comparable evaluate(ChildNodes arguments, Assignments assignments, AutomaticallyDefinedFunctions adfs) {
      Comparable first = arguments.first().evaluate(assignments, adfs);
      Comparable second = arguments.second().evaluate(assignments, adfs);
      return first.compareTo(second) > 0 ? first : second;
   }

   @Override
   public Signature getSignature() {
      return signature;
   }

   @Override
   public Node simplify(FunctionNode functionNode) {
      return Min.simplifyMinMax(functionNode, this, Min.getSingleton());
   }
}
