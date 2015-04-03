package org.oakgp;

import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerType;

import org.junit.Test;
import org.oakgp.function.Function;
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
   private static final Function[] FUNCTIONS = new Function[] { new Add(), new Subtract(), new Multiply(), new If(), new LessThan(), new LessThanOrEqual(),
         new GreaterThan(), new GreaterThanOrEqual(), new Equal(), new NotEqual() };

   @Test
   public void testNext() {
      Random mockRandom = mock(Random.class);
      // mock randomly selecting one of the 4 functions in OPERATORS with an integer return type
      given(mockRandom.nextInt(4)).willReturn(1, 0, 2, 1, 2, 0, 3);
      // mock randomly selecting one of the 6 functions in OPERATORS with a boolean return type
      given(mockRandom.nextInt(6)).willReturn(1, 0, 5, 4);

      FunctionSet functionSet = new FunctionSet(mockRandom, FUNCTIONS);

      // TODO test with more than just integerType()
      assertSame(FUNCTIONS[1], functionSet.next(integerType()));
      assertSame(FUNCTIONS[0], functionSet.next(integerType()));
      assertSame(FUNCTIONS[2], functionSet.next(integerType()));
      assertSame(FUNCTIONS[1], functionSet.next(integerType()));
      assertSame(FUNCTIONS[2], functionSet.next(integerType()));
      assertSame(FUNCTIONS[0], functionSet.next(integerType()));
      assertSame(FUNCTIONS[3], functionSet.next(integerType()));

      assertSame(FUNCTIONS[5], functionSet.next(booleanType()));
      assertSame(FUNCTIONS[4], functionSet.next(booleanType()));
      assertSame(FUNCTIONS[9], functionSet.next(booleanType()));
      assertSame(FUNCTIONS[8], functionSet.next(booleanType()));
   }

   @Test
   public void testNextAlternative() {
      Random mockRandom = mock(Random.class);
      FunctionSet functionSet = new FunctionSet(mockRandom, FUNCTIONS);

      given(mockRandom.nextInt(3)).willReturn(0, 1, 2, 0);
      given(mockRandom.nextInt(2)).willReturn(0, 1);
      assertSame(FUNCTIONS[1], functionSet.nextAlternative(FUNCTIONS[0]));
      assertSame(FUNCTIONS[1], functionSet.nextAlternative(FUNCTIONS[0]));
      assertSame(FUNCTIONS[2], functionSet.nextAlternative(FUNCTIONS[0]));
      assertSame(FUNCTIONS[2], functionSet.nextAlternative(FUNCTIONS[0]));

      given(mockRandom.nextInt(3)).willReturn(0, 1, 1, 2);
      given(mockRandom.nextInt(2)).willReturn(0, 1);
      assertSame(FUNCTIONS[0], functionSet.nextAlternative(FUNCTIONS[1]));
      assertSame(FUNCTIONS[0], functionSet.nextAlternative(FUNCTIONS[1]));
      assertSame(FUNCTIONS[2], functionSet.nextAlternative(FUNCTIONS[1]));
      assertSame(FUNCTIONS[2], functionSet.nextAlternative(FUNCTIONS[1]));

      given(mockRandom.nextInt(3)).willReturn(0, 1, 2, 2);
      given(mockRandom.nextInt(2)).willReturn(0, 1);
      assertSame(FUNCTIONS[0], functionSet.nextAlternative(FUNCTIONS[2]));
      assertSame(FUNCTIONS[1], functionSet.nextAlternative(FUNCTIONS[2]));
      assertSame(FUNCTIONS[0], functionSet.nextAlternative(FUNCTIONS[2]));
      assertSame(FUNCTIONS[1], functionSet.nextAlternative(FUNCTIONS[2]));
   }
}
