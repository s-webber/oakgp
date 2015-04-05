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
   // TODO move to PrimitiveSetTest

   private static final Function[] FUNCTIONS = new Function[] { new Add(), new Subtract(), new Multiply(), new If(), new LessThan(), new LessThanOrEqual(),
         new GreaterThan(), new GreaterThanOrEqual(), new Equal(), new NotEqual() };

   @Test
   public void testNext() {
      Random mockRandom = mock(Random.class);
      // mock randomly selecting one of the 4 functions in OPERATORS with an integer return type
      given(mockRandom.nextInt(4)).willReturn(1, 0, 2, 1, 2, 0, 3);
      // mock randomly selecting one of the 6 functions in OPERATORS with a boolean return type
      given(mockRandom.nextInt(6)).willReturn(1, 0, 5, 4);

      PrimitiveSet functionSet = createPrimitiveSet(mockRandom);

      // TODO test with more than just integerType()
      assertSame(FUNCTIONS[1], functionSet.nextFunction(integerType()));
      assertSame(FUNCTIONS[0], functionSet.nextFunction(integerType()));
      assertSame(FUNCTIONS[2], functionSet.nextFunction(integerType()));
      assertSame(FUNCTIONS[1], functionSet.nextFunction(integerType()));
      assertSame(FUNCTIONS[2], functionSet.nextFunction(integerType()));
      assertSame(FUNCTIONS[0], functionSet.nextFunction(integerType()));
      assertSame(FUNCTIONS[3], functionSet.nextFunction(integerType()));

      assertSame(FUNCTIONS[5], functionSet.nextFunction(booleanType()));
      assertSame(FUNCTIONS[4], functionSet.nextFunction(booleanType()));
      assertSame(FUNCTIONS[9], functionSet.nextFunction(booleanType()));
      assertSame(FUNCTIONS[8], functionSet.nextFunction(booleanType()));
   }

   @Test
   public void testNextAlternative() {
      Random mockRandom = mock(Random.class);
      PrimitiveSet functionSet = createPrimitiveSet(mockRandom);

      given(mockRandom.nextInt(3)).willReturn(0, 1, 2, 0);
      given(mockRandom.nextInt(2)).willReturn(0, 1);
      assertSame(FUNCTIONS[1], functionSet.nextFunction(FUNCTIONS[0]));
      assertSame(FUNCTIONS[1], functionSet.nextFunction(FUNCTIONS[0]));
      assertSame(FUNCTIONS[2], functionSet.nextFunction(FUNCTIONS[0]));
      assertSame(FUNCTIONS[2], functionSet.nextFunction(FUNCTIONS[0]));

      given(mockRandom.nextInt(3)).willReturn(0, 1, 1, 2);
      given(mockRandom.nextInt(2)).willReturn(0, 1);
      assertSame(FUNCTIONS[0], functionSet.nextFunction(FUNCTIONS[1]));
      assertSame(FUNCTIONS[0], functionSet.nextFunction(FUNCTIONS[1]));
      assertSame(FUNCTIONS[2], functionSet.nextFunction(FUNCTIONS[1]));
      assertSame(FUNCTIONS[2], functionSet.nextFunction(FUNCTIONS[1]));

      given(mockRandom.nextInt(3)).willReturn(0, 1, 2, 2);
      given(mockRandom.nextInt(2)).willReturn(0, 1);
      assertSame(FUNCTIONS[0], functionSet.nextFunction(FUNCTIONS[2]));
      assertSame(FUNCTIONS[1], functionSet.nextFunction(FUNCTIONS[2]));
      assertSame(FUNCTIONS[0], functionSet.nextFunction(FUNCTIONS[2]));
      assertSame(FUNCTIONS[1], functionSet.nextFunction(FUNCTIONS[2]));
   }

   private PrimitiveSet createPrimitiveSet(Random random) {
      FunctionSet.Builder builder = new FunctionSet.Builder();

      builder.put("+", FUNCTIONS[0]);
      builder.put("-", FUNCTIONS[1]);
      builder.put("*", FUNCTIONS[2]);

      builder.put("<", FUNCTIONS[4]);
      builder.put("<=", FUNCTIONS[5]);
      builder.put(">", FUNCTIONS[6]);
      builder.put(">=", FUNCTIONS[7]);
      builder.put("=", FUNCTIONS[8]);
      builder.put("!=", FUNCTIONS[9]);

      builder.put("if", FUNCTIONS[3]);

      return new PrimitiveSet(builder.build(), null, null, random, .1);
   }
}
