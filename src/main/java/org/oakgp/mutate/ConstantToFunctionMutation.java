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
package org.oakgp.mutate;

import org.oakgp.GeneticOperator;
import org.oakgp.TreeGenerator;
import org.oakgp.node.Node;
import org.oakgp.node.NodeType;
import org.oakgp.select.NodeSelector;
import org.oakgp.util.Random;

public final class ConstantToFunctionMutation implements GeneticOperator {
   private final Random random;
   private final TreeGenerator treeGenerator;

   public ConstantToFunctionMutation(Random random, TreeGenerator treeGenerator) {
      this.random = random;
      this.treeGenerator = treeGenerator;
   }

   @Override
   public Node evolve(NodeSelector selector) {
      Node root = selector.next();
      int nodeCount = root.getNodeCount(NodeType::isTerminal);
      int index = random.nextInt(nodeCount);
      return root.replaceAt(index, n -> treeGenerator.generate(n.getType(), 2), NodeType::isTerminal);
   }
}
