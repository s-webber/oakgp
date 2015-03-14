package org.oakgp.operator;

public final class GreaterThan extends ComparisonOperator {
	public GreaterThan() {
		super(false);
	}

	@Override
	protected boolean evaluate(int arg1, int arg2) {
		return arg1 > arg2;
	}
}
