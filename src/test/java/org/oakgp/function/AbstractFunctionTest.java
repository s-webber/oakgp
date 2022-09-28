/*
 * Copyright 2015 S. Webber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oakgp.function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.oakgp.TestUtils.asSet;
import static org.oakgp.type.CommonTypes.booleanListType;
import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.doubleType;
import static org.oakgp.type.CommonTypes.integerListType;
import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.listType;
import static org.oakgp.type.CommonTypes.stringType;
import static org.oakgp.util.Utils.createIntegerTypeArray;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.oakgp.Assignments;
import org.oakgp.NodeSimplifier;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;
import org.oakgp.primitive.ConstantSet;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.primitive.VariableSet;
import org.oakgp.serialize.NodeReader;
import org.oakgp.type.Types.Type;

public abstract class AbstractFunctionTest {
   private static final Type[] DEFAULT_VARIABLE_TYPES = createIntegerTypeArray(100);

   /**
    * Used to check that every call to 'evaluate' and 'simplify' has a corresponding call to 'to'.
    * <p>
    * Makes it possible to spot when someone accidently writes code like {@code simplify("(min v0 v0)");} or {@code evaluate("(min 7 8)");}.
    */
   private final AtomicInteger unfinishedExpectationsCtr = new AtomicInteger();
   private static FunctionSet functionSet;

   /**
    * Observable allows other objects to be notified of the tests that are run.
    * <p>
    * This is used to support the automatic creation of http://www.oakgp.org/functions
    */
   private final Observable observable = new Observable() {
      @Override
      public void notifyObservers(Object arg) {
         super.setChanged();
         super.notifyObservers(arg);
      }
   };

   protected abstract Function getFunction();

   @Before
   public void before() {
      unfinishedExpectationsCtr.set(0);
   }

   @After
   public void after() {
      if (unfinishedExpectationsCtr.get() != 0) {
         throw new RuntimeException("Some expectations were never finished. Check that each 'evaluate' and 'simplify' has a corresponding 'to'.");
      }
   }

   @Test
   public abstract void testEvaluate();

   @Test
   public abstract void testCanSimplify();

   @Test
   public abstract void testCannotSimplify();

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

   protected FunctionSet getFunctionSet() { // TODO make private
      if (functionSet == null) {
         functionSet = createFunctionSet();
      }
      return functionSet;
   }

   private FunctionSet createFunctionSet() {
      List<FunctionSet.Key> keys = new ArrayList<>();
      Set<Type> types = asSet(integerType(), doubleType(), stringType(), booleanType(), integerListType(), booleanListType(), listType(doubleType()));
      for (Function f : FunctionsTest.getTestFunctions()) {
         keys.addAll(FunctionKeyFactory.createKeys(f, types));
      }
      return new FunctionSet(keys);
   }

   protected void cannotSimplify(String input, Type... variableTypes) {
      FunctionNode node = readFunctionNode(input, variableTypes);
      assertSame(node, NodeSimplifier.simplify(node));
   }

   void addObserver(Observer o) {
      observable.addObserver(o);
   }

   private FunctionNode readFunctionNode(String input, Type... variableTypes) {
      return readFunctionNode(input, VariableSet.createVariableSet(variableTypes));
   }

   private FunctionNode readFunctionNode(String input, VariableSet variableSet) {
      FunctionNode functionNode = (FunctionNode) readNode(input, variableSet);
      assertSame(getFunction().getClass(), functionNode.getFunction().getClass());
      return functionNode;
   }

   private Node readNode(String input, VariableSet variableSet) {
      try (NodeReader nodeReader = new NodeReader(input, getFunctionSet(), new ConstantSet(), variableSet)) {
         return nodeReader.readNode();
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }

   public EvaluateExpectation evaluate(String input) {
      return new EvaluateExpectation(input);
   }

   protected class EvaluateExpectation {
      private final int id;
      private final String input;
      private ConstantNode[] assignedValues = {};

      private EvaluateExpectation(String input) {
         this.id = unfinishedExpectationsCtr.incrementAndGet();
         this.input = input;
      }

      public EvaluateExpectation assigned(ConstantNode... assignedValues) {
         this.assignedValues = assignedValues;
         return this;
      }

      public void to(Object expectedResult) {
         if (id != unfinishedExpectationsCtr.getAndDecrement()) {
            throw new RuntimeException("'to' called more than once for a 'evaluate'.");
         }

         Type[] variableTypes = toVariableTypes(assignedValues);
         FunctionNode functionNode = readFunctionNode(input, variableTypes);
         Assignments assignments = toAssignments(assignedValues);

         // assert evaluate consistently returns the expected result
         assertEquals(expectedResult, functionNode.evaluate(assignments, null));
         assertEquals(expectedResult, functionNode.evaluate(assignments, null));

         // assert simplified version of input matches the expected result
         Node simplifiedNode = NodeSimplifier.simplify(functionNode);
         assertEquals(expectedResult, simplifiedNode.evaluate(assignments, null));
         // if no assigned values then assert simplifies to a terminal node (i.e. node count = 1)
         if (assignedValues.length == 0) {
            assertEquals(1, simplifiedNode.getNodeCount());
            assertEquals(expectedResult, simplifiedNode.evaluate(null, null));
         }

         observable.notifyObservers(new Notification(functionNode, assignedValues, expectedResult));
      }

      private Assignments toAssignments(ConstantNode[] constants) {
         Object[] values = new Object[constants.length];
         for (int i = 0; i < constants.length; i++) {
            values[i] = constants[i].evaluate(null, null);
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

   static class Notification {
      final FunctionNode input;
      final ConstantNode[] assignedValues;
      final Object output;

      private Notification(FunctionNode input, ConstantNode[] assignedValues, Object output) {
         this.input = input;
         this.assignedValues = assignedValues;
         this.output = output;
      }
   }

   public SimplifyExpectation simplify(String input) {
      return new SimplifyExpectation(input);
   }

   protected class SimplifyExpectation {
      private int id;
      private final String input;
      private Type[] variableTypes = DEFAULT_VARIABLE_TYPES;
      private FunctionNode inputNode;
      private Node simplifiedNode;

      public SimplifyExpectation(String input) {
         this.id = unfinishedExpectationsCtr.incrementAndGet();
         this.input = input;
      }

      public SimplifyExpectation with(Type... variableTypes) {
         this.variableTypes = variableTypes;
         return this;
      }

      public SimplifyExpectation to(String expected) {
         if (id != unfinishedExpectationsCtr.getAndDecrement()) {
            throw new RuntimeException("'to' called more than once for a 'simplify'.");
         }

         // parse input and expected String representations to create Node objects
         VariableSet variableSet = VariableSet.createVariableSet(variableTypes);
         Node expectedNode = readNode(expected, variableSet);
         inputNode = readFunctionNode(input, variableSet);
         simplifiedNode = NodeSimplifier.simplify(inputNode);

         // assert simplified version of input matches expected
         if (!expectedNode.equals(simplifiedNode)) {
            FunctionNode fn1 = (FunctionNode) expectedNode;
            FunctionNode fn2 = (FunctionNode) simplifiedNode;
            System.out.println("expected " + fn1.getFunction() + " " + variableSet.getById(0).getType());
            System.out.println("actual " + fn2.getFunction());
            System.out.println(fn1.getFunction() == fn2.getFunction());
            VariableNode vn1 = (VariableNode) fn1.getChildren().first();
            VariableNode vn2 = (VariableNode) fn2.getChildren().first();
            System.out.println(vn1 + " " + vn1.getType());
            System.out.println(vn2 + " " + vn2.getType());
            System.out.println(vn1 == vn2);
            // System.exit(1);
         }
         assertEquals(expectedNode, simplifiedNode);
         assertEquals(expectedNode.hashCode(), simplifiedNode.hashCode());
         assertSame(inputNode.getType(), simplifiedNode.getType());

         // if the simplified version is a function then assert that:
         // 1. its type is compatible with the return type of the function being tested
         // 2. its arguments are compatible with the signature of the function being tested
         if (simplifiedNode instanceof FunctionNode) {
            FunctionNode functionNode = (FunctionNode) simplifiedNode;
            Signature functionNodeSignature = functionNode.getFunction().getSignature();

            assertTrue(functionNode.getType().isAssignable(functionNodeSignature.getReturnType()));
            assertAssignableArgumentTypes(functionNode.getChildren(), functionNodeSignature);
         }

         // assert multiple calls to simplify with the same argument produces results that are equal
         assertEquals(NodeSimplifier.simplify(inputNode), NodeSimplifier.simplify(inputNode));

         return this;
      }

      private void assertAssignableArgumentTypes(ChildNodes args, Signature signature) {
         assertEquals(args.size(), signature.getArgumentTypesLength());
         for (int i = 0; i < signature.getArgumentTypesLength(); i++) {
            Type from = args.getNode(i).getType();
            Type to = signature.getArgumentType(i);
            assertTrue("cannot assign from " + from + " to " + to + " " + (from == to), from.isAssignable(to));
         }
      }

      public SimplifyExpectation verify(Object... values) {
         Assignments assignments = Assignments.createAssignments(values);
         Object expectedOutcome = inputNode.evaluate(assignments, null);
         Object actualOutcome = simplifiedNode.evaluate(assignments, null);
         assertEquals(expectedOutcome, actualOutcome);
         return this;
      }

      public void verifyAll(Object[][] values) {
         for (Object[] a : values) {
            verify(a);
         }
      }
   }

   @Test
   public final void testBooleanFunctionExpectationsBuilder() {
      assertEquals(getFunction() instanceof BooleanFunction, createBooleanFunctionExpectationsBuilder() != null);
   }

   @Test
   public final void testBooleanFunctionGetOpposite() {
      BooleanFunctionExpectationsBuilder expectations = createBooleanFunctionExpectationsBuilder();
      if (expectations == null) {
         return;
      }

      FunctionNode input = readFunctionNode(expectations.input, expectations.variableSet);
      Node expected = expectations.opposite == null ? null : readNode(expectations.opposite, expectations.variableSet);
      assertEquals(expected, ((BooleanFunction) getFunction()).getOpposite(input));
   }

   @Test
   public final void testBooleanFunctionGetIncompatibles() {
      BooleanFunctionExpectationsBuilder expectations = createBooleanFunctionExpectationsBuilder();
      if (expectations == null) {
         return;
      }

      FunctionNode input = readFunctionNode(expectations.input, expectations.variableSet);
      Set<Node> expectedIncompatibles = new HashSet<>();
      for (String s : expectations.incompatibles) {
         expectedIncompatibles.add(readNode(s, expectations.variableSet));
      }
      Set<Node> actualIncompatibles = ((BooleanFunction) getFunction()).getIncompatibles(input);
      assertEquals(expectedIncompatibles, actualIncompatibles);
      assertEquals(expectations.incompatibles.length, expectedIncompatibles.size());
      if (expectations.opposite != null) {
         assertFalse(actualIncompatibles.contains(readNode(expectations.opposite, expectations.variableSet)));
      }
   }

   @Test
   public final void testBooleanFunctionGetConsequences() {
      BooleanFunctionExpectationsBuilder expectations = createBooleanFunctionExpectationsBuilder();
      if (expectations == null) {
         return;
      }

      FunctionNode input = readFunctionNode(expectations.input, expectations.variableSet);
      Set<Node> expectedConsequences = new HashSet<>();
      for (String s : expectations.consequences) {
         expectedConsequences.add(readNode(s, expectations.variableSet));
      }
      assertEquals(expectedConsequences, ((BooleanFunction) getFunction()).getConsequences(input));
      assertEquals(expectations.consequences.length, expectedConsequences.size());
   }

   @Test
   public final void testBooleanFunctionGetUnion() {
      BooleanFunctionExpectationsBuilder expectations = createBooleanFunctionExpectationsBuilder();
      if (expectations == null || expectations.unionOther == null) {
         return;
      }

      FunctionNode inputA = readFunctionNode(expectations.input, expectations.variableSet);
      FunctionNode inputB = readFunctionNode(expectations.unionOther, expectations.variableSet);
      Node expected = readNode(expectations.unionOutcome, expectations.variableSet);
      assertEquals(expected, ((BooleanFunction) getFunction()).getUnion(inputA, inputB));
   }

   protected BooleanFunctionExpectationsBuilder createBooleanFunctionExpectationsBuilder() {
      return null;
   }

   protected class BooleanFunctionExpectationsBuilder {
      private final String input;
      private final VariableSet variableSet;
      private String opposite;
      private String[] incompatibles = new String[0];
      private String[] consequences = new String[0];
      private String unionOther;
      private String unionOutcome;

      public BooleanFunctionExpectationsBuilder(String input) {
         this(input, integerType(), integerType(), integerType());
      }

      public BooleanFunctionExpectationsBuilder(String input, Type... types) {
         this.input = input;
         this.variableSet = VariableSet.createVariableSet(types);
      }

      public BooleanFunctionExpectationsBuilder opposite(String opposite) {
         this.opposite = opposite;
         return this;
      }

      public BooleanFunctionExpectationsBuilder incompatibles(String... incompatibles) {
         this.incompatibles = incompatibles;
         return this;
      }

      public BooleanFunctionExpectationsBuilder consequences(String... consequences) {
         this.consequences = consequences;
         return this;
      }

      public BooleanFunctionExpectationsBuilder union(String unionOther, String unionOutcome) {
         this.unionOther = unionOther;
         this.unionOutcome = unionOutcome;
         return this;
      }
   }
}
