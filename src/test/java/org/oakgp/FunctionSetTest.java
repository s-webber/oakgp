package org.oakgp;

import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.oakgp.Type.BOOLEAN;
import static org.oakgp.Type.INTEGER;

import org.junit.Test;
import org.oakgp.function.Operator;
import org.oakgp.function.choice.If;
import org.oakgp.function.compare.Equal;
import org.oakgp.function.compare.GreaterThan;
import org.oakgp.function.compare.GreaterThanOrEqual;
import org.oakgp.function.compare.LessThan;
import org.oakgp.function.compare.LessThanOrEqual;
import org.oakgp.function.compare.NotEqual;
import org.oakgp.function.math.Add;
import org.oakgp.function.math.Multiply;
import org.oakgp.function.math.Subtract;
import org.oakgp.util.Random;

public class FunctionSetTest {
   private static final Operator[] OPERATORS = new Operator[] { new Add(), new Subtract(), new Multiply(), new If(), new LessThan(), new LessThanOrEqual(),
      new GreaterThan(), new GreaterThanOrEqual(), new Equal(), new NotEqual() };

   @Test
   public void testNext() {
      Random mockRandom = mock(Random.class);
      // mock randomly selecting one of the 4 operators in OPERATORS with an integer return type
      given(mockRandom.nextInt(4)).willReturn(1, 0, 2, 1, 2, 0, 3);
      // mock randomly selecting one of the 6 operators in OPERATORS with a boolean return type
      given(mockRandom.nextInt(6)).willReturn(1, 0, 5, 4);

      FunctionSet functionSet = new FunctionSet(mockRandom, OPERATORS);

      // TODO test with more than just INTEGER
      assertSame(OPERATORS[1], functionSet.next(INTEGER));
      assertSame(OPERATORS[0], functionSet.next(INTEGER));
      assertSame(OPERATORS[2], functionSet.next(INTEGER));
      assertSame(OPERATORS[1], functionSet.next(INTEGER));
      assertSame(OPERATORS[2], functionSet.next(INTEGER));
      assertSame(OPERATORS[0], functionSet.next(INTEGER));
      assertSame(OPERATORS[3], functionSet.next(INTEGER));

      assertSame(OPERATORS[5], functionSet.next(BOOLEAN));
      assertSame(OPERATORS[4], functionSet.next(BOOLEAN));
      assertSame(OPERATORS[9], functionSet.next(BOOLEAN));
      assertSame(OPERATORS[8], functionSet.next(BOOLEAN));
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
