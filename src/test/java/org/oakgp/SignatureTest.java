package org.oakgp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.oakgp.Signature.createSignature;
import static org.oakgp.Type.BOOLEAN;
import static org.oakgp.Type.INTEGER;

import org.junit.Test;

public class SignatureTest {
	@Test
	public void test() {
		Signature signature = createSignature(INTEGER, BOOLEAN, INTEGER, BOOLEAN);

		assertEquals(3, signature.getArgumentTypesLength());
		assertSame(INTEGER, signature.getReturnType());
		assertSame(BOOLEAN, signature.getArgumentType(0));
		assertSame(INTEGER, signature.getArgumentType(1));
		assertSame(BOOLEAN, signature.getArgumentType(2));

		assertArrayIndexOutOfBoundsException(signature, -1);
		assertArrayIndexOutOfBoundsException(signature, 3);
	}

	private void assertArrayIndexOutOfBoundsException(Signature signature, int index) {
		try {
			signature.getArgumentType(index);
			fail();
		} catch (ArrayIndexOutOfBoundsException e) {
			// expected
		}
	}
}
