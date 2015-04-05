package org.oakgp;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.oakgp.TestUtils.assertConstant;
import static org.oakgp.TestUtils.assertVariable;
import static org.oakgp.TestUtils.createTypeArray;
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.Type.integerType;

import org.junit.Test;
import org.oakgp.node.ConstantNode;
import org.oakgp.util.Random;

public class TerminalSetTest {
   // TODO move to PrimitiveSetTest
   // TODO add extra tests for when: a) numVariables=0, b) numVariables=1 and c) constants.length=0

   private static final double VARIABLE_RATIO = .6;
   private static final ConstantNode[] CONSTANTS = { integerConstant(7), integerConstant(8), integerConstant(9) };
   private static final Type[] VARIABLE_TYPES = createTypeArray(3);

   @Test
   public void testNext() {
      Random mockRandom = mock(Random.class);
      given(mockRandom.nextDouble()).willReturn(0.0, VARIABLE_RATIO, VARIABLE_RATIO + .01, .9, VARIABLE_RATIO - .01, .7);
      given(mockRandom.nextInt(3)).willReturn(1, 0, 2, 1, 0, 2);

      PrimitiveSet terminalSet = createPrimitiveSet(mockRandom);

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

      PrimitiveSet terminalSet = createPrimitiveSet(mockRandom);

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

      PrimitiveSet terminalSet = createPrimitiveSet(mockRandom);

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

   private PrimitiveSet createPrimitiveSet(Random random) {
      ConstantSet constantSet = new ConstantSet(CONSTANTS);
      VariableSet variableSet = VariableSet.createVariableSet(VARIABLE_TYPES);
      return new PrimitiveSet(null, constantSet, variableSet, random, VARIABLE_RATIO);
   }
}
