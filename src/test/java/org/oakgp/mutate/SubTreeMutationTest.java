package org.oakgp.mutate;

import static org.oakgp.TestUtils.assertNodeEquals;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.TestUtils.readNode;
import static org.oakgp.util.DummyRandom.GetIntExpectation.nextInt;

import org.junit.Test;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.select.DummyNodeSelector;

public class SubTreeMutationTest {
   @Test
   public void test() {
      Node input = readNode("(+ (+ (if (zero? v0) 7 8) v1) (+ 9 v2))");
      DummyNodeSelector selector = new DummyNodeSelector(input);
      ConstantNode result = integerConstant(42);
      SubTreeMutation mutator = new SubTreeMutation(nextInt(10).returns(5), (t, d) -> result);
      assertNodeEquals("(+ (+ (if (zero? v0) 7 8) 42) (+ 9 v2))", mutator.evolve(selector));
   }
}
