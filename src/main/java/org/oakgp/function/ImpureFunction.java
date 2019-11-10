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

/**
 * Represents an impure function.
 * <p>
 * An impure function is a function that conforms to at least one of the following criteria:
 * <ol>
 * <li>Does not always return the same result when given the same arguments.</li>
 * <li>Can cause observable side effects (e.g. mutating state).</li>
 * </ol>
 */
public interface ImpureFunction extends Function {
   /**
    * Returns {@code false}.
    * <p>
    * Returns {@code false} so that the node simplification process can recognise that this is an impure function and therefore function nodes that use it
    * cannot be replaced with their value.
    *
    * @see org.oakgp.NodeSimplifier
    */
   @Override
   default boolean isPure() {
      return false;
   }
}
