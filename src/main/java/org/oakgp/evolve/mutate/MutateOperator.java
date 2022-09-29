package org.oakgp.evolve.mutate;

import org.oakgp.generate.TreeGenerator;
import org.oakgp.node.Node;
import org.oakgp.primitive.PrimitiveSet;
import org.oakgp.util.Random;

public interface MutateOperator {
   Node mutate(Node input, PrimitiveSet primitiveSet, TreeGenerator treeGenerator, Random random);
}
