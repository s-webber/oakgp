package org.oakgp;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.oakgp.TestUtils.assertConstant;
import static org.oakgp.TestUtils.assertVariable;
import static org.oakgp.TestUtils.createConstant;
import static org.oakgp.TestUtils.createTypeArray;
import static org.oakgp.TestUtils.createVariable;

import org.junit.Test;
import org.oakgp.node.ConstantNode;
import org.oakgp.util.Random;

public class TerminalSetTest {
   // TODO add extra tests for when: a) numVariables=0, b) numVariables=1 and c) constants.length=0

   private static final double VARIABLE_RATIO = .6;
   private static final ConstantNode[] CONSTANTS = { createConstant(7), createConstant(8), createConstant(9) };
   private static final Type[] VARIABLE_TYPES = createTypeArray(3);

   @Test
   public void testNext() {
      Random mockRandom = mock(Random.class);
      given(mockRandom.nextDouble()).willReturn(0.0, VARIABLE_RATIO, VARIABLE_RATIO + .01, .9, VARIABLE_RATIO - .01, .7);
      given(mockRandom.nextInt(3)).willReturn(1, 0, 2, 1, 0, 2);

      TerminalSet terminalSet = new TerminalSet(mockRandom, VARIABLE_RATIO, VARIABLE_TYPES, CONSTANTS);

      assertVariable(1, terminalSet.next());
      assertConstant(7, terminalSet.next());
      assertConstant(9, terminalSet.next());
      assertConstant(8, terminalSet.next());
      assertVariable(0, terminalSet.next());
      assertConstant(9, terminalSet.next());
   }

   @Test
   public void testNextAlternativeConstant() {
      Random mockRandom = mock(Random.class);
      given(mockRandom.nextDouble()).willReturn(VARIABLE_RATIO); // force constants to be produced

      TerminalSet terminalSet = new TerminalSet(mockRandom, VARIABLE_RATIO, VARIABLE_TYPES, CONSTANTS);

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

      TerminalSet terminalSet = new TerminalSet(mockRandom, VARIABLE_RATIO, VARIABLE_TYPES, CONSTANTS);

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
      assertVariable(2, terminalSet.nextAlternative(createConstant(9)));
      assertVariable(0, terminalSet.nextAlternative(createConstant(9)));
      assertVariable(1, terminalSet.nextAlternative(createConstant(9)));
   }
}
