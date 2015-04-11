package org.oakgp.fitness;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.oakgp.Assignments.createAssignments;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.oakgp.Assignments;
import org.oakgp.node.Node;

public class TestDataFitnessFunctionTest {
   @Test
   public void testDefaultRankingFunction() {
      // test data
      Map<Assignments, Integer> testData = new HashMap<>();
      Assignments assignments1 = createAssignments(1);
      testData.put(assignments1, 9);
      Assignments assignments2 = createAssignments(2);
      testData.put(assignments2, 2);
      Assignments assignments3 = createAssignments(3);
      testData.put(assignments3, 7);

      // mock
      Node mockNode = mock(Node.class);
      given(mockNode.evaluate(assignments1)).willReturn(12);
      given(mockNode.evaluate(assignments2)).willReturn(-1);
      given(mockNode.evaluate(assignments3)).willReturn(5);

      // invoke evaluate method
      FitnessFunction fitnessFunction = new TestDataFitnessFunction(testData);
      double result = fitnessFunction.evaluate(mockNode);

      // assert result
      assertEquals(8d, result, 0d);
   }

   @Test
   public void testSpecifiedRankingFunction() {
      // test data
      Map<Assignments, Integer> testData = new HashMap<>();
      Assignments assignments1 = createAssignments(1);
      testData.put(assignments1, 9);
      Assignments assignments2 = createAssignments(2);
      testData.put(assignments2, 2);
      Assignments assignments3 = createAssignments(3);
      testData.put(assignments3, 7);

      // mock
      Node mockNode = mock(Node.class);
      given(mockNode.evaluate(assignments1)).willReturn("abcdefg");
      given(mockNode.evaluate(assignments2)).willReturn("asdf");
      given(mockNode.evaluate(assignments3)).willReturn("qwerty");

      // invoke evaluate method
      FitnessFunction fitnessFunction = new TestDataFitnessFunction(testData, (o) -> ((String) o).length());
      double result = fitnessFunction.evaluate(mockNode);

      // assert result
      assertEquals(5d, result, 0d);
   }
}
