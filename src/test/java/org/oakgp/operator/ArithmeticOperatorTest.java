package org.oakgp.operator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.oakgp.Type.INTEGER;

import org.junit.Test;
import org.oakgp.Signature;

public class ArithmeticOperatorTest {
	@Test
	public void testGetSignature() {
		Operator o = new ArithmeticOperator() {
			@Override
			protected int evaluate(int arg1, int arg2) {
				throw new UnsupportedOperationException();
			}
		};
		Signature signature = o.getSignature();
		assertSame(INTEGER, signature.getReturnType());
		assertEquals(2, signature.getArgumentTypesLength());
		assertSame(INTEGER, signature.getArgumentType(0));
		assertSame(INTEGER, signature.getArgumentType(1));
	}
}
