package org.oakgp.operator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.oakgp.Type.BOOLEAN;
import static org.oakgp.Type.INTEGER;

import org.junit.Test;
import org.oakgp.Signature;

public class ComparisonOperatorTest {
	@Test
	public void testGetSignature() {
		Operator o = new ComparisonOperator(true) {
			@Override
			protected boolean evaluate(int arg1, int arg2) {
				throw new UnsupportedOperationException();
			}
		};
		Signature signature = o.getSignature();
		assertSame(BOOLEAN, signature.getReturnType());
		assertEquals(2, signature.getArgumentTypesLength());
		assertSame(INTEGER, signature.getArgumentType(0));
		assertSame(INTEGER, signature.getArgumentType(1));
	}
}
