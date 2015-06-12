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

   protected abstract void getCanSimplifyTests(SimplifyTestCases testCases);

   @Test
   public abstract void testCannotSimplify();

   @Test
   public abstract void testEvaluate();

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

   public EvaluateExpectation evaluate(String input) {
      return new EvaluateExpectation(input);
   }

   protected class EvaluateExpectation {
      private final String input;
      private ConstantNode[] assignedValues = {};

      private EvaluateExpectation(String input) {
         this.input = input;
      }

      public EvaluateExpectation assigned(ConstantNode... assignedValues) {
         this.assignedValues = assignedValues;
         return this;
      }

      public void to(Object expectedResult) {
         Type[] variableTypes = toVariableTypes(assignedValues);
         FunctionNode functionNode = readFunctionNode(input, variableTypes);
         Assignments assignments = toAssignments(assignedValues);
         // assert evaluate consistently returns the expected result
         assertEquals(expectedResult, functionNode.evaluate(assignments));
         assertEquals(expectedResult, functionNode.evaluate(assignments));
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
   }

   protected Function[] getFunctionSet() {
      return new Function[] { getFunction() };
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
