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
package org.oakgp.generate;

import org.oakgp.Type;
import org.oakgp.node.Node;

/**
 * Creates tree data structures.
 * <p>
 * Can be used to create randomly generate the initial population of a genetic programming run.
 */
@FunctionalInterface
public interface TreeGenerator {
   /**
    * Constructs a new tree data structure.
    *
    * @param type
    *           the required return type of the tree
    * @param depth
    *           the maximum depth of any nodes of the tree
    * @return the newly created tree data structure
    */
   Node generate(Type type, int depth);
}
