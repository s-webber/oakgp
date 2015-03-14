package org.oakgp.operator;

public final class GreaterThanOrEqual extends ComparisonOperator {
	public GreaterThanOrEqual() {
		super(true);
	}

	@Override
	protected boolean evaluate(int arg1, int arg2) {
		return arg1 >= arg2;
	}
}
