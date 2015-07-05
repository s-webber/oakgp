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
package org.oakgp.evolve;

import org.oakgp.node.Node;
import org.oakgp.select.NodeSelector;

/** Creates new {@code Node} instances evolved from existing instances. */
@FunctionalInterface
public interface GeneticOperator {
   /**
    * Returns a new {@code Node} evolved from existing instances.
    *
    * @param selector
    *           used to select the existing instances to use as a basis for evolving a new instance
    * @return a new {@code Node} evolved from existing instances obtained from {@code selector}
    */
   Node evolve(NodeSelector selector);
}
