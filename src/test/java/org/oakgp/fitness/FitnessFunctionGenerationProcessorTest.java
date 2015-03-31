package org.oakgp;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.oakgp.TestUtils.assertRankedCandidate;
import static org.oakgp.TestUtils.createConstant;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.oakgp.node.Node;

public class FitnessFunctionGenerationProcessorTest {
	@Test
	public void test() {
		// test data
		Node a = createConstant(1);
		double aFitness = 9;
		Node b = createConstant(2);
		double bFitness = 12;
		Node c = createConstant(3);
		double cFitness = 8;
		List<Node> input = Arrays.asList(a, b, c);

		// mock
		FitnessFunction mockFitnessFunction = mock(FitnessFunction.class);
		given(mockFitnessFunction.evaluate(a)).willReturn(aFitness);
		given(mockFitnessFunction.evaluate(b)).willReturn(bFitness);
		given(mockFitnessFunction.evaluate(c)).willReturn(cFitness);

		// invoke process method
		GenerationProcessor generationProcessor = new FitnessFunctionGenerationProcessor(mockFitnessFunction);
		List<RankedCandidate> output = generationProcessor.process(input);

		// assert output
		assertRankedCandidate(output.get(0), c, cFitness);
		assertRankedCandidate(output.get(1), a, aFitness);
		assertRankedCandidate(output.get(2), b, bFitness);
	}
}
