package org.oakgp.generate;

import org.oakgp.node.Node;
import org.oakgp.node.ProgramNode;
import org.oakgp.type.Types.Type;

public class AdfTreeGenerator implements TreeGenerator {
   private final TreeGenerator[] treeGenerators;
   private final Type[] returnTypes;

   public AdfTreeGenerator(TreeGenerator[] treeGenerators, Type[] returnTypes) {
      this.treeGenerators = treeGenerators;
      this.returnTypes = returnTypes;
   }

   @Override
   public Node generate(Type type, int depth) { // TODO consider method signature
      Node[] trees = new Node[treeGenerators.length];
      for (int i = 0; i < trees.length; i++) {
         trees[i] = treeGenerators[i].generate(returnTypes[i], depth);
      }
      return new ProgramNode(trees);
   }
}
