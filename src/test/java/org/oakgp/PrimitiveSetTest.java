package org.oakgp;

import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.oakgp.TestUtils.assertConstant;
import static org.oakgp.TestUtils.assertVariable;
import static org.oakgp.TestUtils.createTypeArray;
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.TestUtils.integerConstant;
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
import org.oakgp.node.ConstantNode;
import org.oakgp.util.Random;

public class PrimitiveSetTest {
   // TODO add extra tests for when: a) numVariables=0, b) numVariables=1 and c) constants.length=0

   private static final double VARIABLE_RATIO = .6;
   private static final ConstantNode[] CONSTANTS = { integerConstant(7), integerConstant(8), integerConstant(9) };
   private static final Type[] VARIABLE_TYPES = createTypeArray(3);
   private static final Function[] FUNCTIONS = new Function[] { new Add(), new Subtract(), new Multiply(), new If(), new LessThan(), new LessThanOrEqual(),
      new GreaterThan(), new GreaterThanOrEqual(), new Equal(), new NotEqual() };

   @Test
   public void testNextFunctionByType() {
      Random mockRandom = mock(Random.class);
      // mock randomly selecting one of the 4 functions in OPERATORS with an integer return type
      given(mockRandom.nextInt(4)).willReturn(1, 0, 2, 1, 2, 0, 3);
      // mock randomly selecting one of the 6 functions in OPERATORS with a boolean return type
      given(mockRandom.nextInt(6)).willReturn(1, 0, 5, 4);

      PrimitiveSet functionSet = createWithFunctions(mockRandom);

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
   public void testNextFunctionByFunction() {
      Random mockRandom = mock(Random.class);
      PrimitiveSet functionSet = createWithFunctions(mockRandom);

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

   @Test
   public void testNextTerminal() {
      Random mockRandom = mock(Random.class);
      given(mockRandom.nextDouble()).willReturn(0.0, VARIABLE_RATIO, VARIABLE_RATIO + .01, .9, VARIABLE_RATIO - .01, .7);
      given(mockRandom.nextInt(3)).willReturn(1, 0, 2, 1, 0, 2);

      PrimitiveSet terminalSet = createWithTerminals(mockRandom);

      // TODO test with more than just integerType()
      assertVariable(1, terminalSet.nextTerminal(integerType()));
      assertConstant(7, terminalSet.nextTerminal(integerType()));
      assertConstant(9, terminalSet.nextTerminal(integerType()));
      assertConstant(8, terminalSet.nextTerminal(integerType()));
      assertVariable(0, terminalSet.nextTerminal(integerType()));
      assertConstant(9, terminalSet.nextTerminal(integerType()));
   }

   @Test
   public void testNextAlternativeConstant() {
      Random mockRandom = mock(Random.class);
      given(mockRandom.nextDouble()).willReturn(VARIABLE_RATIO); // force constants to be produced

      PrimitiveSet terminalSet = createWithTerminals(mockRandom);

      given(mockRandom.nextInt(3)).willReturn(0, 1, 2, 0);
      given(mockRandom.nextInt(2)).willReturn(0, 1);
      assertConstant(8, terminalSet.nextAlternative(CONSTANTS[0]));
      assertConstant(8, terminalSet.nextAlternative(CONSTANTS[0]));
      assertConstant(9, terminalSet.nextAlternative(CONSTANTS[0]));
      assertConstant(9, terminalSet.nextAlternative(CONSTANTS[0]));

      given(mockRandom.nextInt(3)).willReturn(0, 1, 1, 2);
      given(mockRandom.nextInt(2)).willReturn(0, 1);
      assertConstant(7, terminalSet.nextAlternative(CONSTANTS[1]));
      assertConstant(7, terminalSet.nextAlternative(CONSTANTS[1]));
      assertConstant(9, terminalSet.nextAlternative(CONSTANTS[1]));
      assertConstant(9, terminalSet.nextAlternative(CONSTANTS[1]));

      given(mockRandom.nextInt(3)).willReturn(0, 1, 2, 2);
      given(mockRandom.nextInt(2)).willReturn(0, 1);
      assertConstant(7, terminalSet.nextAlternative(CONSTANTS[2]));
      assertConstant(8, terminalSet.nextAlternative(CONSTANTS[2]));
      assertConstant(7, terminalSet.nextAlternative(CONSTANTS[2]));
      assertConstant(8, terminalSet.nextAlternative(CONSTANTS[2]));

      given(mockRandom.nextInt(3)).willReturn(2, 0, 1);
      assertConstant(9, terminalSet.nextAlternative(createVariable(9)));
      assertConstant(7, terminalSet.nextAlternative(createVariable(9)));
      assertConstant(8, terminalSet.nextAlternative(createVariable(9)));
   }

   @Test
   public void testNextAlternativeVariable() {
      Random mockRandom = mock(Random.class);
      given(mockRandom.nextDouble()).willReturn(VARIABLE_RATIO - .01); // force variables to be produced

      PrimitiveSet terminalSet = createWithTerminals(mockRandom);

      given(mockRandom.nextInt(3)).willReturn(0, 1, 2, 0);
      given(mockRandom.nextInt(2)).willReturn(0, 1);
      assertVariable(1, terminalSet.nextAlternative(createVariable(0)));
      assertVariable(1, terminalSet.nextAlternative(createVariable(0)));
      assertVariable(2, terminalSet.nextAlternative(createVariable(0)));
      assertVariable(2, terminalSet.nextAlternative(createVariable(0)));

      given(mockRandom.nextInt(3)).willReturn(0, 1, 1, 2);
      given(mockRandom.nextInt(2)).willReturn(0, 1);
      assertVariable(0, terminalSet.nextAlternative(createVariable(1)));
      assertVariable(0, terminalSet.nextAlternative(createVariable(1)));
      assertVariable(2, terminalSet.nextAlternative(createVariable(1)));
      assertVariable(2, terminalSet.nextAlternative(createVariable(1)));

      given(mockRandom.nextInt(3)).willReturn(0, 1, 2, 2);
      given(mockRandom.nextInt(2)).willReturn(0, 1);
      assertVariable(0, terminalSet.nextAlternative(createVariable(2)));
      assertVariable(1, terminalSet.nextAlternative(createVariable(2)));
      assertVariable(0, terminalSet.nextAlternative(createVariable(2)));
      assertVariable(1, terminalSet.nextAlternative(createVariable(2)));

      given(mockRandom.nextInt(3)).willReturn(2, 0, 1);
      assertVariable(2, terminalSet.nextAlternative(integerConstant(9)));
      assertVariable(0, terminalSet.nextAlternative(integerConstant(9)));
      assertVariable(1, terminalSet.nextAlternative(integerConstant(9)));
   }

   private PrimitiveSet createWithTerminals(Random random) {
      ConstantSet constantSet = new ConstantSet(CONSTANTS);
      VariableSet variableSet = VariableSet.createVariableSet(VARIABLE_TYPES);
      return new PrimitiveSet(null, constantSet, variableSet, random, VARIABLE_RATIO);
   }

   private PrimitiveSet createWithFunctions(Random random) {
      FunctionSet.Builder builder = new FunctionSet.Builder();

      builder.put(FUNCTIONS[0]);
      builder.put(FUNCTIONS[1]);
      builder.put(FUNCTIONS[2]);

      builder.put(FUNCTIONS[4]);
      builder.put(FUNCTIONS[5]);
      builder.put(FUNCTIONS[6]);
      builder.put(FUNCTIONS[7]);
      builder.put(FUNCTIONS[8]);
      builder.put(FUNCTIONS[9]);

      builder.put(FUNCTIONS[3]);

      return new PrimitiveSet(builder.build(), null, null, random, .1);
   }
}
