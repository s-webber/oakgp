/*
 * Copyright 2019 S. Webber
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
package org.oakgp.examples.art;

import static org.oakgp.type.CommonTypes.integerType;

import org.oakgp.function.math.IntegerUtils;
import org.oakgp.generate.TreeGeneratorImpl;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.primitive.ConstantSet;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.primitive.PrimitiveSet;
import org.oakgp.primitive.PrimitiveSetImpl;
import org.oakgp.primitive.VariableSet;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;
import org.oakgp.util.FunctionSetBuilder;
import org.oakgp.util.JavaUtilRandomAdapter;
import org.oakgp.util.Utils;

public class ArtFactory {
   static Type COLOUR_TYPE = Types.declareType("colour");
   private static int TREE_DEPTH = 7;

   private final JavaUtilRandomAdapter random;
   private final PrimitiveSet primitiveSet;

   ArtFactory() {
      this.random = new JavaUtilRandomAdapter();
      FunctionSet functions = new FunctionSetBuilder() //
            .add(new CreateColour()) //
            .add(IntegerUtils.INTEGER_UTILS.getAdd()) //
            .add(IntegerUtils.INTEGER_UTILS.getMultiply()) //
            .add(IntegerUtils.INTEGER_UTILS.getSubtract()) //
            .build();
      ConstantSet constants = new ConstantSet(Utils.createIntegerConstants(-5, 5).toArray(new ConstantNode[0]));
      VariableSet variables = VariableSet.createVariableSet(integerType(), integerType());
      this.primitiveSet = new PrimitiveSetImpl(functions, constants, variables, random, .7);
   }

   FunctionNode generate() {
      return (FunctionNode) TreeGeneratorImpl.grow(primitiveSet, random).generate(COLOUR_TYPE, TREE_DEPTH);
   }

   /** Replace child of root node with new subtree. */
   FunctionNode mutate(FunctionNode input) {
      int replacementSubTreeIndex = selectRootNodeChildIndex();
      Node replacementSubTree = TreeGeneratorImpl.grow(primitiveSet, random).generate(integerType(), TREE_DEPTH - 1);
      ChildNodes updatedChildNodes = input.getChildren().replaceAt(replacementSubTreeIndex, replacementSubTree);
      System.out.println(input.getChildren().getNode(replacementSubTreeIndex));
      System.out.println(replacementSubTree);
      return new FunctionNode(input, updatedChildNodes);
   }

   public FunctionNode crossover(FunctionNode x, FunctionNode y) {
      int replacementSubTreeIndex = selectRootNodeChildIndex();
      Node replacementSubTree = y.getChildren().getNode(replacementSubTreeIndex);
      ChildNodes updatedChildNodes = x.getChildren().replaceAt(replacementSubTreeIndex, replacementSubTree);
      System.out.println(x.getChildren().getNode(replacementSubTreeIndex));
      System.out.println(replacementSubTree);
      return new FunctionNode(x, updatedChildNodes);
   }

   private int selectRootNodeChildIndex() {
      return random.nextInt(3); // 3 as root node will have 3 child nodes (red, green and blue)
   }
}
