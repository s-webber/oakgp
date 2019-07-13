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

import static org.oakgp.type.CommonTypes.booleanType;

import org.oakgp.Arguments;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.Node;

/** Represents an operation. */
public interface Function {
   /**
    * Returns the result of applying this operation to the specified {@code Arguments}.
    *
    * @param arguments
    *           represents the arguments to apply to the operation
    * @return the result of applying this operation to the {@code arguments}
    */
   Object evaluate(Arguments arguments);

   /** Returns the return type and argument types associated with this function. */
   Signature getSignature();

   /**
    * Attempts to find a simplified alternative to applying this function to the specified arguments.
    * <p>
    * Simplification can occur by replacing expressions with constant values (e.g. replacing {@code (+ 1 1)} with {@code 2}) or removing redundant branches
    * (e.g. replacing {@code (if (< 2 3) (+ v0 v1) (* v0 v1))} with {@code (+ v0 v1)}.
    *
    * @param arguments
    *           the arguments to be applied to (i.e. evaluated by) this function
    * @return a simplified version of applying this function to the specified arguments, or {@code null} if unable to simplify.
    * @see org.oakgp.NodeSimplifier
    */
   default Node simplify(ChildNodes children) {
      return null;
   }

   /**
    * Returns the {@code String} value to use in the textual representation of this function.
    * <p>
    * Converts class names to kebab-case function names. Replaces "is-" prefix with "?" suffix in names of boolean functions.
    */
   default String getDisplayName() {
      StringBuilder sb = new StringBuilder();

      String className = getClass().getSimpleName();
      for (int i = 0; i < className.length(); i++) {
         char c = className.charAt(i);
         if (Character.isUpperCase(c)) {
            if (i != 0) {
               sb.append('-');
            }
            sb.append(Character.toLowerCase(c));
         } else {
            sb.append(c);
         }
      }

      String result = sb.toString();
      if (getSignature().getReturnType() == booleanType() && result.startsWith("is-")) {
         return sb.substring(3) + '?';
      } else {
         return result;
      }
   }

   /**
    * Returns {@code true} if this function is pure, else returns {@code false}.
    * <p>
    * A function is a pure function if it conforms to the following criteria:
    * <ol>
    * <li><i>Always</i> returns the same result when given the <i>same</i> arguments.</li>
    * <li>Doesn't cause any <i>observable</i> side effects.</li>
    * </ol>
    * </p>
    * <p>
    * If a function is pure then it is <i>referentially transparent</i>. This means that if a function node contains a pure function, and all its arguments are
    * constants, then the function node can be replaced with the result of evaluating it. This avoids unnecessary computation and reduces bloat.
    * </p>
    * <p>
    * Default implementation always returns {@code true}.
    *
    * @see ImpureFunction
    */
   default boolean isPure() {
      return true;
   }
}
