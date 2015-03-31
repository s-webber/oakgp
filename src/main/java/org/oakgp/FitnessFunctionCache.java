package org.oakgp;

import static org.oakgp.util.CacheMap.createCache;

import java.util.Map;

import org.oakgp.node.Node;

public final class FitnessFunctionCache implements FitnessFunction {
	private final FitnessFunction fitnessFunction;
	private final Map<Node, Double> cache;

	public FitnessFunctionCache(int maxSize, FitnessFunction fitnessFunction) {
		this.fitnessFunction = fitnessFunction;
		this.cache = createCache(maxSize);
	}

	@Override
	public double evaluate(Node n) {
		Double result = cache.get(n);
		if (result == null) {
			result = fitnessFunction.evaluate(n);
			cache.put(n, result);
		}
		return result;
	}
}
