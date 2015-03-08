package org.oakgp;

import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.oakgp.operator.Add;
import org.oakgp.operator.Multiply;
import org.oakgp.operator.Operator;
import org.oakgp.operator.Subtract;
import org.oakgp.util.Random;

public class FunctionSetTest {
	private static final Operator[] OPERATORS = new Operator[] { new Add(), new Subtract(), new Multiply() };

	@Test
	public void testNext() {
		Random mockRandom = mock(Random.class);
		given(mockRandom.nextInt(3)).willReturn(1, 0, 2, 1, 2, 0);

		FunctionSet functionSet = new FunctionSet(mockRandom, OPERATORS);

		assertSame(OPERATORS[1], functionSet.next());
		assertSame(OPERATORS[0], functionSet.next());
		assertSame(OPERATORS[2], functionSet.next());
		assertSame(OPERATORS[1], functionSet.next());
		assertSame(OPERATORS[2], functionSet.next());
		assertSame(OPERATORS[0], functionSet.next());
	}

	@Test
	public void testNextAlternative() {
		Random mockRandom = mock(Random.class);
		FunctionSet functionSet = new FunctionSet(mockRandom, OPERATORS);

		given(mockRandom.nextInt(3)).willReturn(0, 1, 2, 0);
		given(mockRandom.nextInt(2)).willReturn(0, 1);
		assertSame(OPERATORS[1], functionSet.nextAlternative(OPERATORS[0]));
		assertSame(OPERATORS[1], functionSet.nextAlternative(OPERATORS[0]));
		assertSame(OPERATORS[2], functionSet.nextAlternative(OPERATORS[0]));
		assertSame(OPERATORS[2], functionSet.nextAlternative(OPERATORS[0]));

		given(mockRandom.nextInt(3)).willReturn(0, 1, 1, 2);
		given(mockRandom.nextInt(2)).willReturn(0, 1);
		assertSame(OPERATORS[0], functionSet.nextAlternative(OPERATORS[1]));
		assertSame(OPERATORS[0], functionSet.nextAlternative(OPERATORS[1]));
		assertSame(OPERATORS[2], functionSet.nextAlternative(OPERATORS[1]));
		assertSame(OPERATORS[2], functionSet.nextAlternative(OPERATORS[1]));

		given(mockRandom.nextInt(3)).willReturn(0, 1, 2, 2);
		given(mockRandom.nextInt(2)).willReturn(0, 1);
		assertSame(OPERATORS[0], functionSet.nextAlternative(OPERATORS[2]));
		assertSame(OPERATORS[1], functionSet.nextAlternative(OPERATORS[2]));
		assertSame(OPERATORS[0], functionSet.nextAlternative(OPERATORS[2]));
		assertSame(OPERATORS[1], functionSet.nextAlternative(OPERATORS[2]));
	}
}
