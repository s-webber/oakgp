package org.oakgp;

import org.oakgp.node.Node;

/** Calculates the fitness of a potential solution. */
@FunctionalInterface
public interface FitnessFunction {
	double evaluate(Node n);
}
