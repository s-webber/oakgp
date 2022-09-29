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

import org.oakgp.generate.TreeGenerator;
import org.oakgp.node.Node;
import org.oakgp.node.NodeType;
import org.oakgp.node.walk.StrategyWalk;
import org.oakgp.primitive.PrimitiveSet;
import org.oakgp.util.Random;

/** Replaces a randomly selected terminal node of the parent with a newly generated subtree. */
public final class ConstantToFunctionMutation implements MutateOperator {
   @Override
   public Node mutate(Node input, PrimitiveSet primitiveSet, TreeGenerator treeGenerator, Random random) {
      int nodeCount = StrategyWalk.getNodeCount(input, NodeType::isTerminal);
      int index = random.nextInt(nodeCount);
      return StrategyWalk.replaceAt(input, index, n -> treeGenerator.generate(n.getType(), 2), NodeType::isTerminal);
   }
}
