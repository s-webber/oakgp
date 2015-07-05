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
package org.oakgp.evaluate;

import java.util.Collection;
import java.util.List;

import org.oakgp.node.Node;

/** Ranks and sorts the fitness of {@code Node} instances. */
@FunctionalInterface
public interface GenerationProcessor {
   /**
    * Returns the sorted result of evaluating the fitness of each of the specified nodes.
    *
    * @param input
    *           the {@code Node} instances to evaluate the fitness of
    * @return a {@code List} of {@code RankedCandidate} - one for each {@code Node} specified in {@code input} - sorted by fitness
    */
   List<RankedCandidate> process(Collection<Node> input);
}
