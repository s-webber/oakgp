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
package org.oakgp.evolve.crossover;

import org.oakgp.evolve.GeneticOperator;
import org.oakgp.node.Node;
import org.oakgp.select.NodeSelector;
import org.oakgp.util.Random;
import org.oakgp.util.Utils;

/**
 * Replaces a subtree in one parent with the subtree at the corresponding position of another parent.
 * <p>
 * One-point crossover is a homologous operation - i.e. the structure of the resulting tree has a similar structure to the trees that were combined to produce
 * it.
 * </p>
 *
 * @see <a href="http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.49.5419&rep=rep1&type=pdf">Genetic Programming with One-Point Crossover and Point
 *      Mutation</a>
 */
public final class OnePointCrossover implements GeneticOperator {
   private final Random random;

   public OnePointCrossover(Random random) {
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
