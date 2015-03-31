package org.oakgp.operator.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.oakgp.TestUtils.createTypeArray;
import static org.oakgp.TestUtils.readNode;
import static org.oakgp.TestUtils.writeNode;
import static org.oakgp.Type.INTEGER;
import static org.oakgp.examples.SystemTestUtils.ARITHMETIC_FUNCTION_SET;
import static org.oakgp.examples.SystemTestUtils.RANDOM;
import static org.oakgp.examples.SystemTestUtils.RATIO_VARIABLES;
import static org.oakgp.examples.SystemTestUtils.createConstants;
import static org.oakgp.examples.SystemTestUtils.makeRandomTree;
import static org.oakgp.util.Utils.assertEvaluateToSameResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.oakgp.NodeSimplifier;
import org.oakgp.Signature;
import org.oakgp.TerminalSet;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.operator.Operator;

public class ArithmeticOperatorTest {
	private static final File TEST_DATA_FILE = new File("src/test/resources/ArithmeticOperatorTest.txt");
	private static final int TEST_DATA_SIZE = 10000;

	@Test
	public void testGetSignature() {
		Operator o = new ArithmeticOperator() {
			@Override
			protected int evaluate(int arg1, int arg2) {
				throw new UnsupportedOperationException();
			}
		};
		Signature signature = o.getSignature();
		assertSame(INTEGER, signature.getReturnType());
		assertEquals(2, signature.getArgumentTypesLength());
		assertSame(INTEGER, signature.getArgumentType(0));
		assertSame(INTEGER, signature.getArgumentType(1));
	}

	@Test
	public void testSimplification() throws IOException {
		List<String> tests = getTestData();
		assertEquals(TEST_DATA_SIZE, tests.size());
		long inputNodeCtr = 0;
		long outputNodeCtr = 0;
		long start = System.currentTimeMillis();
		for (String test : tests) {
			Node input = readNode(test);
			Node output = new NodeSimplifier().simplify(input);
			assertEvaluateToSameResult(input, output);
			inputNodeCtr += input.getNodeCount();
			outputNodeCtr += output.getNodeCount();
		}
		long end = System.currentTimeMillis();
		System.out.println("from " + inputNodeCtr + " to " + outputNodeCtr + " in " + (end - start) + "ms");
	}

	private List<String> getTestData() throws IOException {
		if (!TEST_DATA_FILE.exists()) {
			createTestData(TEST_DATA_FILE, TEST_DATA_SIZE);
		}
		return Files.readAllLines(TEST_DATA_FILE.toPath());
	}

	private static void createTestData(File outputFile, int size) throws IOException {
		ConstantNode[] constants = createConstants(11);
		int numVariables = 4;
		TerminalSet terminalSet = new TerminalSet(RANDOM, RATIO_VARIABLES, createTypeArray(numVariables), constants);
		Set<String> examples = new HashSet<>();
		try (FileWriter fw = new FileWriter(outputFile)) {
			while (examples.size() < size) {
				Node node = makeRandomTree(ARITHMETIC_FUNCTION_SET, terminalSet, 16);
				String example = writeNode(node);
				if (node.getNodeCount() > 7 && examples.add(example)) {
					fw.write(example);
					fw.write('\n');
				}
			}
		}
	}
}
