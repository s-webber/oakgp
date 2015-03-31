package org.oakgp.operator;

public final class LessThanOrEqual extends ComparisonOperator {
	public LessThanOrEqual() {
		super(true);
	}

	@Override
	protected boolean evaluate(int arg1, int arg2) {
		return arg1 <= arg2;
	}
}