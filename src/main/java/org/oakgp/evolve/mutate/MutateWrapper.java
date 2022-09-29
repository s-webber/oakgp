package org.oakgp.evolve.mutate;

import org.oakgp.evolve.GeneticOperator;
import org.oakgp.generate.TreeGenerator;
import org.oakgp.node.Node;
import org.oakgp.primitive.PrimitiveSet;
import org.oakgp.select.NodeSelector;
import org.oakgp.util.Random;

public final class MutateWrapper implements GeneticOperator {
   private final MutateOperator mutateOperator;
   private final PrimitiveSet primitiveSet;
   private final TreeGenerator treeGenerator;
   private final Random random;

   public MutateWrapper(MutateOperator mutateOperator, PrimitiveSet primitiveSet, TreeGenerator treeGenerator, Random random) {
      this.mutateOperator = mutateOperator;
      this.primitiveSet = primitiveSet;
      this.treeGenerator = treeGenerator;
      this.random = random;
   }

   @Override
   public Node evolve(NodeSelector selector) {
      return mutateOperator.mutate(selector.next(), primitiveSet, treeGenerator, random);
   }
}
