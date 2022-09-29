package org.oakgp.evolve.crossover;

import org.oakgp.evolve.GeneticOperator;
import org.oakgp.evolve.mutate.MutateOperator;
import org.oakgp.generate.TreeGenerator;
import org.oakgp.node.Node;
import org.oakgp.node.ProgramNode;
import org.oakgp.primitive.PrimitiveSet;
import org.oakgp.select.NodeSelector;
import org.oakgp.util.Random;

public final class AdfMutateWrapper implements GeneticOperator {
   private final MutateOperator mutateOperator;
   private final PrimitiveSet[] primitiveSets;
   private final TreeGenerator[] treeGenerators;
   private final Random random;

   public AdfMutateWrapper(MutateOperator mutateOperator, PrimitiveSet[] primitiveSets, TreeGenerator[] treeGenerators, Random random) {
      this.mutateOperator = mutateOperator;
      this.primitiveSets = primitiveSets;
      this.treeGenerators = treeGenerators;
      this.random = random;
   }

   @Override
   public Node evolve(NodeSelector selector) {
      ProgramNode input = (ProgramNode) selector.next();

      int branch = selectBranch(input);

      Node newBranch = mutateOperator.mutate(input.getAbstractDefinedFunction(branch), primitiveSets[branch], treeGenerators[branch], random);

      Node[] newBranches = new Node[input.adfCount()];
      for (int i = 0; i < newBranches.length; i++) {
         newBranches[i] = i == branch ? newBranch : input.getAbstractDefinedFunction(i);
      }
      return new ProgramNode(newBranches);
   }

   private int selectBranch(ProgramNode input) {
      int branchCount = input.adfCount();
      int randomInt = random.nextInt(input.getNodeCount());
      for (int i = 0, count = 0; i < branchCount - 1; i++, count += input.getAbstractDefinedFunction(i).getNodeCount()) {
         if (count > randomInt) {
            return i;
         }
      }
      return branchCount - 1;
   }
}
