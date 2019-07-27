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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.oakgp.TestUtils.createFunctionSet;
import static org.oakgp.node.NodeType.isFunction;
import static org.oakgp.util.Utils.createIntegerTypeArray;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Observable;
import java.util.Observer;

import org.junit.Test;
import org.oakgp.Assignments;
import org.oakgp.NodeSimplifier;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.primitive.VariableSet;
import org.oakgp.serialize.NodeReader;
import org.oakgp.type.Types.Type;

public abstract class AbstractFunctionTest {
   private static final Type[] DEFAULT_VARIABLE_TYPES = createIntegerTypeArray(100);

   private final FunctionSet functionSet;

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

   protected AbstractFunctionTest() {
      functionSet = getFunctionSet();
   }

   protected abstract Function getFunction();

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

   protected FunctionSet getFunctionSet() {
      return createFunctionSet(getFunction());
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
      try (NodeReader nodeReader = new NodeReader(input, functionSet, new ConstantNode[0], variableSet)) {
         return nodeReader.readNode();
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
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
         observable.notifyObservers(new Notification(functionNode, assignedValues, expectedResult));
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
      private final String input;
      private Type[] variableTypes = DEFAULT_VARIABLE_TYPES;
      private FunctionNode inputNode;
      private Node simplifiedNode;

      public SimplifyExpectation(String input) {
         this.input = input;
      }

      public SimplifyExpectation with(Type... variableTypes) {
         this.variableTypes = variableTypes;
         return this;
      }

      public SimplifyExpectation to(String expected) {
         VariableSet variableSet = VariableSet.createVariableSet(variableTypes);

         Node expectedNode = readNode(expected, variableSet);
         inputNode = readFunctionNode(input, variableSet);
         simplifiedNode = NodeSimplifier.simplify(inputNode);

         // assert actual matched expected
         assertEquals(expectedNode, simplifiedNode);
         assertSame(inputNode.getType(), simplifiedNode.getType());

         if (isFunction(simplifiedNode)) {
            // assert that signature of function matches the
            // return type and argument types of the function node the function belongs to
            FunctionNode functionNode = (FunctionNode) simplifiedNode;
            Signature functionNodeSignature = functionNode.getFunction().getSignature();

            assertSame(functionNode.getType(), functionNodeSignature.getReturnType());
            assertSameArgumentTypes(functionNode.getChildren(), functionNodeSignature);
         }

         // assert multiple calls to simplify with the same argument produces results that are equal
         assertEquals(NodeSimplifier.simplify(inputNode), NodeSimplifier.simplify(inputNode));

         return this;
      }

      private void assertSameArgumentTypes(ChildNodes args, Signature signature) {
         assertEquals(args.size(), signature.getArgumentTypesLength());
         for (int i = 0; i < signature.getArgumentTypesLength(); i++) {
            assertSame(args.getNode(i).getType(), signature.getArgumentType(i));
         }
      }

      public SimplifyExpectation verify(Object... values) {
         Assignments assignments = Assignments.createAssignments(values);
         Object expectedOutcome = inputNode.evaluate(assignments);
         Object actualOutcome = simplifiedNode.evaluate(assignments);
         assertEquals(expectedOutcome, actualOutcome);
         return this;
      }

      public void verifyAll(Object[][] values) {
         for (Object[] a : values) {
            verify(a);
         }
      }
   }
}
