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
package org.oakgp.crossover;

import org.oakgp.GeneticOperator;
import org.oakgp.node.Node;
import org.oakgp.select.NodeSelector;
import org.oakgp.util.Random;
import org.oakgp.util.Utils;

public final class CommonRegionCrossover implements GeneticOperator {
   private final Random random;

   public CommonRegionCrossover(Random random) {
      this.random = random;
   }

   @Override
   public Node evolve(NodeSelector selector) {
      Node parent1 = selector.next();
      Node parent2 = selector.next();
      int commonRegionSize = CommonRegion.getNodeCount(parent1, parent2);
      int crossOverPoint = Utils.selectSubNodeIndex(random, commonRegionSize);
      return CommonRegion.crossoverAt(parent1, parent2, crossOverPoint);
   }
}
