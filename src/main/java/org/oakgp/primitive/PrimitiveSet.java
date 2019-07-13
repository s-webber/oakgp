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
package org.oakgp.primitive;

import org.oakgp.function.Function;
import org.oakgp.node.Node;
import org.oakgp.type.Types.Type;

/** Represents the range of possible functions and terminal nodes to use during a genetic programming run. */
public interface PrimitiveSet {
   /** Returns {@code true} if the primitive set contains at least one terminal node of the specified type, else {@code false}. */
   boolean hasTerminals(Type type);

   /** Returns {@code true} if the primitive set contains at least one function which has a return type of the specified type, else {@code false}. */
   boolean hasFunctions(Type type);

   /**
    * Returns a randomly selected terminal node.
    *
    * @return a randomly selected terminal node
    */
   Node nextTerminal(Type type);

   /**
    * Returns a randomly selected terminal node that is not the same as the specified {@code Node}.
    *
    * @param current
    *           the current {@code Node} that the returned result should be an alternative to (i.e. not the same as)
    * @return a randomly selected terminal node that is not the same as the specified {@code Node}
    */
   Node nextAlternativeTerminal(Node current);

   /**
    * Returns a randomly selected {@code Function} of the specified {@code Type}.
    *
    * @param type
    *           the required return type of the {@code Function}
    * @return a randomly selected {@code Function} with a return type of {@code type}
    */
   Function nextFunction(Type type);

   /**
    * Returns a randomly selected {@code Function} that is not the same as the specified {@code Function}.
    *
    * @param current
    *           the current {@code Function} that the returned result should be an alternative to (i.e. not the same as)
    * @return a randomly selected {@code Function} that is not the same as the specified {@code Function}
    */
   Function nextAlternativeFunction(Function current);
}
