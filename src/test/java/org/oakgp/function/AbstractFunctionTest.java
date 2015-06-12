package org.oakgp.function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.oakgp.NodeSimplifier.simplify;
import static org.oakgp.TestUtils.createTypeArray;
import static org.oakgp.TestUtils.writeNode;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.oakgp.Assignments;
import org.oakgp.FunctionSet;
import org.oakgp.Type;
import org.oakgp.VariableSet;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.serialize.NodeReader;

public abstract class AbstractFunctionTest {
   private static final Type[] VARIABLES = createTypeArray(100);

   private final FunctionSet functionSet;

   protected AbstractFunctionTest() {
      functionSet = new FunctionSet(getFunctionSet());
   }

   protected abstract Function getFunction();

   protected abstract void getEvaluateTests(EvaluateTestCases testCases);

   protected abstract void getCanSimplifyTests(SimplifyTestCases testCases);

   @Test
   public abstract void testCannotSimplify();

   @Test
   public final void testEvaluate() {
      EvaluateTestCases testCases = new EvaluateTestCases();
      getEvaluateTests(testCases);
      assertFalse(testCases.tests.isEmpty());
      for (EvaluateTest test : testCases.tests) {
         Assignments assignments = toAssignments(test.constants);
         Type[] variableTypes = toVariableTypes(test.constants);

         FunctionNode input = readInput(test.input, variableTypes);
         Object actualResult = input.evaluate(assignments);

         assertEquals(test.expectedOutput, actualResult);
      }
   }

   @Test
   public final void testCanSimplify() {
      SimplifyTestCases testCases = new SimplifyTestCases();
      getCanSimplifyTests(testCases);
      assertFalse(testCases.tests.isEmpty());
      for (SimplifyTest test : testCases.tests) {
         FunctionNode input = test.input;
         // TODO use version of readNode that accepts function and variable set
         Node expectedResult = test.expectedOutput;
         Node actualResult = simplify(input);
         assertEquals(writeNode(expectedResult), writeNode(actualResult));
         // TODO assertEquals(expectedResult, actualResult);
         assertSame(actualResult, simplify(actualResult));
         assertEquals(actualResult, simplify(input)); // test get same result from multiple calls to simplify with the same input

         assertNodesEvaluateSameOutcome(test, input, actualResult);
      }
   }

   @Test
   public void testSignatureReused() {
      Function function = getFunction();
      assertNotNull(function.getSignature());
      assertSame(function.getSignature(), function.getSignature());
   }

   @Test
   public void testDisplayNameValid() {
      String displayName = getFunction().getDisplayName();
      assertTrue(NodeReader.isValidDisplayName(displayName));
   }

   private FunctionNode readInput(String input, Type... variableTypes) {
      Node node = readNode(input, variableTypes);
      assertSame(node.toString(), FunctionNode.class, node.getClass());
      FunctionNode functionNode = (FunctionNode) node;
      assertSame(getFunction().getClass(), functionNode.getFunction().getClass());
      return functionNode;
   }

   protected Function[] getFunctionSet() {
      return new Function[] { getFunction() };
   }

   private Assignments toAssignments(ConstantNode[] constants) {
      Object[] values = new Object[constants.length];
      for (int i = 0; i < constants.length; i++) {
         values[i] = constants[i].evaluate(null);
      }
      return Assignments.createAssignments(values);
   }

   private Type[] toVariableTypes(ConstantNode[] constants) {
      Type[] types = new Type[constants.length];
      for (int i = 0; i < constants.length; i++) {
         types[i] = constants[i].getType();
      }
      return types;
   }

   private void assertNodesEvaluateSameOutcome(SimplifyTest test, Node input, Node actualResult) {
      for (Object[] a : test.assignedValues) {
         Assignments assignments = Assignments.createAssignments(a);
         Object expectedOutcome = input.evaluate(assignments);
         Object actualOutcome = actualResult.evaluate(assignments);
         assertEquals(expectedOutcome, actualOutcome);
      }
   }

   protected void cannotSimplify(String input, Type... variableTypes) {
      FunctionNode node = readFunctionNode(input, variableTypes);
      assertSame(node, simplify(node));
   }

   private FunctionNode readFunctionNode(String input) { // TODO remove this method
      return readFunctionNode(input, VARIABLES);
   }

   private FunctionNode readFunctionNode(String input, Type... variableTypes) {
      FunctionNode functionNode = (FunctionNode) readNode(input, variableTypes);
      assertSame(getFunction().getClass(), functionNode.getFunction().getClass());
      return functionNode;
   }

   private Node readNode(String input) { // TODO remove this method
      return readNode(input, VARIABLES);
   }

   private Node readNode(String input, Type... variableTypes) {
      VariableSet variableSet = VariableSet.createVariableSet(variableTypes);
      try (NodeReader nodeReader = new NodeReader(input, functionSet, variableSet)) {
         return nodeReader.readNode();
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }

   protected static class EvaluateTestCases {
      private List<EvaluateTest> tests = new ArrayList<>();

      public void put(String input, Object expectedOutput) {
         tests.add(new EvaluateTest(input, expectedOutput));
      }

      public EvaluateTest when(String input) {
         EvaluateTest test = new EvaluateTest(input);
         tests.add(test);
         return test;
      }

      public List<EvaluateTest> getTests() {
         return tests;
      }
   }

   public static class EvaluateTest {
      private final String input;
      private Object expectedOutput;
      private ConstantNode[] constants = new ConstantNode[0];

      private EvaluateTest(String input, Object expectedOutput) {
         this.input = input;
         this.expectedOutput = expectedOutput;
      }

      private EvaluateTest(String input) {
         this.input = input;
      }

      public EvaluateTest assigned(ConstantNode... constants) {
         this.constants = constants;
         return this;
      }

      public void expect(Object expectedOutput) {
         this.expectedOutput = expectedOutput;
      }
   }

   // TODO use DSL instead overloaded constructor
   protected class SimplifyTestCases {
      private List<SimplifyTest> tests = new ArrayList<>();

      public void put(FunctionNode input, String expectedOutput) {
         put(input, expectedOutput, new Object[0][0]);
      }

      public void put(FunctionNode input, Node expectedOutput) {
         put(input, expectedOutput, new Object[0][0]);
      }

      public void put(String input, String expectedOutput) {
         put(input, expectedOutput, new Object[0][0]);
      }

      public void put(String input, String expectedOutput, Object[] assignedValues) {
         put(input, expectedOutput, new Object[][] { assignedValues });
      }

      public void put(String input, String expectedOutput, Object[][] assignedValues) {
         put(readFunctionNode(input), expectedOutput, assignedValues);
      }

      public void put(FunctionNode input, String expectedOutput, Object[][] assignedValues) {
         put(input, readNode(expectedOutput), assignedValues);
      }

      public void put(FunctionNode input, Node expectedOutput, Object[][] assignedValues) {
         tests.add(new SimplifyTest(input, expectedOutput, assignedValues));
      }
   }

   private static class SimplifyTest {
      final FunctionNode input;
      final Node expectedOutput;
      final Object[][] assignedValues;

      SimplifyTest(FunctionNode input, Node expectedOutput, Object[][] assignedValues) {
         this.input = input;
         this.expectedOutput = expectedOutput;
         this.assignedValues = assignedValues;
      }
   }
}
