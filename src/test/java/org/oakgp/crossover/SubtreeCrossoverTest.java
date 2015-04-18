package org.oakgp.crossover;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.oakgp.TestUtils.writeNode;

import org.junit.Test;
import org.oakgp.NodeEvolver;
import org.oakgp.node.Node;
import org.oakgp.selector.DummyNodeSelector;
import org.oakgp.util.Random;

public class SubtreeCrossoverTest {
   @Test
   public void testFunctionNodes() {
      Random mockRandom = mock(Random.class);
      given(mockRandom.nextInt(2)).willReturn(1);
      given(mockRandom.nextInt(5)).willReturn(3);

      DummyNodeSelector dummySelector = new DummyNodeSelector("(+ 9 5)", "(* 7 (- 8 v5))");

      NodeEvolver c = new SubtreeCrossover(mockRandom);

      Node result = c.evolve(dummySelector);
      assertOutputEquals("(+ 9 (- 8 v5))", result);
      assertTrue(dummySelector.isEmpty());
   }

   @Test
   public void testConstantNodes() {
      Random mockRandom = mock(Random.class);
      given(mockRandom.nextInt(1)).willReturn(0);

      DummyNodeSelector dummySelector = new DummyNodeSelector("1", "2");

      NodeEvolver c = new SubtreeCrossover(mockRandom);

      Node result = c.evolve(dummySelector);
      assertOutputEquals("2", result);
      assertTrue(dummySelector.isEmpty());
   }

   /** Test crossover using trees that use a mix of types (booleans and integers) */
   @Test
   public void testMixedTypes() {
      Random mockRandom = mock(Random.class);
      given(mockRandom.nextInt(2)).willReturn(0, 1);
      given(mockRandom.nextInt(5)).willReturn(0, 0, 1, 2, 3, 4);

      String input = "(+ 4 5)";
      String output = "(if (< 6 7) 8 9)";
      DummyNodeSelector dummySelector = new DummyNodeSelector(input, output, input, output, input, output, input, output, input, output, input, output);

      NodeEvolver c = new SubtreeCrossover(mockRandom);

      assertOutputEquals("(+ 6 5)", c.evolve(dummySelector));
      assertOutputEquals("(+ 4 6)", c.evolve(dummySelector));
      assertOutputEquals("(+ 4 7)", c.evolve(dummySelector));
      assertOutputEquals("(+ 4 8)", c.evolve(dummySelector));
      assertOutputEquals("(+ 4 9)", c.evolve(dummySelector));
      assertOutputEquals("(+ 4 " + output + ")", c.evolve(dummySelector));

      assertTrue(dummySelector.isEmpty());
   }

   /** Test attempted crossover when selected node in first parent has a type that is not present in the second parent */
   @Test
   public void testNoMatchingTypes() {
      Random mockRandom = mock(Random.class);
      given(mockRandom.nextInt(7)).willReturn(2);

      String input = "(+ (if (< 6 7) 8 9) 5)";
      String output = "(+ 1 2)";
      DummyNodeSelector dummySelector = new DummyNodeSelector(input, output);

      NodeEvolver c = new SubtreeCrossover(mockRandom);

      assertOutputEquals(input, c.evolve(dummySelector));
      assertTrue(dummySelector.isEmpty());
   }

   private void assertOutputEquals(String expected, Node actual) {
      assertEquals(expected, writeNode(actual));
   }
}
