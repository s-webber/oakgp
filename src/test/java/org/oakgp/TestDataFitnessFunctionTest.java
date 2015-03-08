package org.oakgp;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.oakgp.Assignments.createAssignments;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.oakgp.node.Node;

public class TestDataFitnessFunctionTest {
	@Test
	public void test() {
		// test data
		Map<Assignments, Integer> testData = new HashMap<>();
		Assignments assignments1 = createAssignments();
		testData.put(assignments1, 9);
		Assignments assignments2 = createAssignments();
		testData.put(assignments2, 2);
		Assignments assignments3 = createAssignments();
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
}
