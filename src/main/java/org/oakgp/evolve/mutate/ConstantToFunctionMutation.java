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
package org.oakgp.evolve.mutate;

import org.oakgp.evolve.GeneticOperator;
import org.oakgp.generate.TreeGenerator;
import org.oakgp.node.Node;
import org.oakgp.node.NodeType;
import org.oakgp.node.walk.StrategyWalk;
import org.oakgp.select.NodeSelector;
import org.oakgp.util.Random;

/** Replaces a randomly selected terminal node of the parent with a newly generated subtree. */
public final class ConstantToFunctionMutation implements GeneticOperator {
   private final Random random;
   private final TreeGenerator treeGenerator;

   /**
    * Creates a {@code ConstantToFunctionMutation} that uses the given {@code TreeGenerator} to generate new subtrees to replace existing terminals.
    *
    * @param random
    *           used to randomly select terminal nodes to mutate
    * @param treeGenerator
    *           used to generate new subtrees to replace terminal nodes selected for mutation
    */
   public ConstantToFunctionMutation(Random random, TreeGenerator treeGenerator) {
      this.random = random;
      this.treeGenerator = treeGenerator;
   }

   @Override
   public Node evolve(NodeSelector selector) {
      Node root = selector.next();
      int nodeCount = StrategyWalk.getNodeCount(root, NodeType::isTerminal);
      int index = random.nextInt(nodeCount);
      return StrategyWalk.replaceAt(root, index, n -> treeGenerator.generate(n.getType(), 2), NodeType::isTerminal);
   }
}
