package org.oakgp.evolve.crossover;

import org.oakgp.evolve.GeneticOperator;
import org.oakgp.node.Node;
import org.oakgp.node.ProgramNode;
import org.oakgp.select.NodeSelector;
import org.oakgp.util.Random;

public final class AdfCrossoverWrapper implements GeneticOperator {
   private final CrossoverOperator crossoverOperator;
   private final Random random;

   public AdfCrossoverWrapper(CrossoverOperator crossoverOperator, Random random) {
      this.crossoverOperator = crossoverOperator;
      this.random = random;
   }

   @Override
   public Node evolve(NodeSelector selector) {
      ProgramNode parent1 = (ProgramNode) selector.next();
      ProgramNode parent2 = (ProgramNode) selector.next();

      int branch = selectBranch(parent1);

      Node newBranch = crossoverOperator.crossover(parent1.getAbstractDefinedFunction(branch), parent2.getAbstractDefinedFunction(branch), random);

      Node[] newBranches = new Node[parent1.adfCount()];
      for (int i = 0; i < newBranches.length; i++) {
         newBranches[i] = i == branch ? newBranch : parent1.getAbstractDefinedFunction(i);
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
