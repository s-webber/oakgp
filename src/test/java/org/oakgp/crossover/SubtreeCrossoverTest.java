package org.oakgp.crossover;

import static org.junit.Assert.assertEquals;
import static org.oakgp.TestUtils.writeNode;

import org.junit.Test;
import org.oakgp.NodeEvolver;
import org.oakgp.node.Node;
import org.oakgp.selector.DummyNodeSelector;
import org.oakgp.util.DummyRandom;

public class SubtreeCrossoverTest {
   @Test
   public void testFunctionNodes() {
      DummyRandom dummyRandom = new DummyRandom.Builder().setInts(2, 1).setInts(5, 3).build();
      DummyNodeSelector dummySelector = new DummyNodeSelector("(+ 9 5)", "(* 7 (- 8 v5))");

      NodeEvolver c = new SubtreeCrossover(dummyRandom);

      Node result = c.evolve(dummySelector);
      assertOutputEquals("(+ 9 (- 8 v5))", result);

      dummyRandom.assertEmpty();
      dummySelector.assertEmpty();
   }

   @Test
   public void testConstantNodes() {
      DummyRandom dummyRandom = new DummyRandom(1, 0);
      DummyNodeSelector dummySelector = new DummyNodeSelector("1", "2");

      NodeEvolver c = new SubtreeCrossover(dummyRandom);

      Node result = c.evolve(dummySelector);
      assertOutputEquals("2", result);
      dummySelector.assertEmpty();
   }

   /** Test crossover using trees that use a mix of types (booleans and integers) */
   @Test
   public void testMixedTypes() {
      String input = "(+ 4 5)";
      String output = "(if (< 6 7) 8 9)";

      DummyRandom dummyRandom = new DummyRandom.Builder().setInts(2, 0, 1, 1, 1, 1, 1).setInts(5, 0, 0, 1, 2, 3, 4).build();
      DummyNodeSelector dummySelector = new DummyNodeSelector(input, output, input, output, input, output, input, output, input, output, input, output);

      NodeEvolver c = new SubtreeCrossover(dummyRandom);

      assertOutputEquals("(+ 6 5)", c.evolve(dummySelector));
      assertOutputEquals("(+ 4 6)", c.evolve(dummySelector));
      assertOutputEquals("(+ 4 7)", c.evolve(dummySelector));
      assertOutputEquals("(+ 4 8)", c.evolve(dummySelector));
      assertOutputEquals("(+ 4 9)", c.evolve(dummySelector));
      assertOutputEquals("(+ 4 " + output + ")", c.evolve(dummySelector));

      dummyRandom.assertEmpty();
      dummySelector.assertEmpty();
   }

   /** Test attempted crossover when selected node in first parent has a type that is not present in the second parent */
   @Test
   public void testNoMatchingTypes() {
      String input = "(+ (if (< 6 7) 8 9) 5)";
      String output = "(+ 1 2)";

      DummyRandom dummyRandom = new DummyRandom(7, 2);
      DummyNodeSelector dummySelector = new DummyNodeSelector(input, output);

      NodeEvolver c = new SubtreeCrossover(dummyRandom);

      assertOutputEquals(input, c.evolve(dummySelector));

      dummyRandom.assertEmpty();
      dummySelector.assertEmpty();
   }

   private void assertOutputEquals(String expected, Node actual) {
      assertEquals(expected, writeNode(actual));
   }
}
