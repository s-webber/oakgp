package org.oakgp.operator;

public final class NotEqual extends ComparisonOperator {
	public NotEqual() {
		super(false);
	}

	@Override
	protected boolean evaluate(int arg1, int arg2) {
		return arg1 != arg2;
	}
}
