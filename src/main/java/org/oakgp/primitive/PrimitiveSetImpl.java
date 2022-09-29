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
package org.oakgp.primitive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.oakgp.function.Signature;
import org.oakgp.node.AutomaticallyDefinedFunction;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;
import org.oakgp.type.Types.Type;
import org.oakgp.util.Random;

/** Represents the range of possible functions and terminal nodes to use during a genetic programming run. */
public final class PrimitiveSetImpl implements PrimitiveSet {
   private final FunctionSet functionSet;
   private final ConstantSet constantSet;
   private final VariableSet variableSet;
   private final Random random;
   private final double ratioVariables;

   /**
    * Constructs a new primitive set consisting of the specified components.
    *
    * @param functionSet
    *           the set of possible functions to use in the construction of programs
    * @param constantSet
    *           the set of possible constants to use in the construction of programs
    * @param variableSet
    *           the set of possible variables to use in the construction of programs
    * @param random
    *           used to randomly select components to use in the construction of programs
    * @param ratioVariables
    *           a value in the range 0 to 1 (inclusive) which specifies the proportion of terminal nodes that should represent variables, rather than constants
    */
   public PrimitiveSetImpl(FunctionSet functionSet, ConstantSet constantSet, VariableSet variableSet, Random random, double ratioVariables) {
      this.functionSet = functionSet;
      this.constantSet = constantSet;
      this.variableSet = variableSet;
      this.random = random;
      this.ratioVariables = ratioVariables;
   }

   @Override
   public boolean hasTerminals(Type type) {
      return !variableSet.getByType(type).isEmpty() || !constantSet.getByType(type).isEmpty();
   }

   @Override
   public boolean hasFunctions(Type type) {
      return !functionSet.getByType(type).isEmpty();
   }

   @Override
   public Node nextTerminal(Type type) {
      boolean doCreateVariable = doCreateVariable();
      Node next = nextTerminal(type, doCreateVariable);
      if (next == null) {
         next = nextTerminal(type, !doCreateVariable);
      }
      if (next == null) {
         throw new IllegalArgumentException("No terminals of type: " + type);
      } else {
         return next;
      }
   }

   private Node nextTerminal(Type type, boolean doCreateVariable) {
      List<? extends Node> possibilities = doCreateVariable ? variableSet.getByType(type) : constantSet.getByType(type);
      return randomlySelectAlternative(null, possibilities);
   }

   @Override
   public Node nextAlternativeTerminal(Node current) {
      boolean doCreateVariable = doCreateVariable();
      Node next = nextAlternativeNode(current, doCreateVariable);
      if (next == current) {
         return nextAlternativeNode(current, !doCreateVariable);
      } else {
         return next;
      }
   }

   private boolean doCreateVariable() {
      return random.nextDouble() < ratioVariables;
   }

   private Node nextAlternativeNode(Node current, boolean doCreateVariable) {
      Type type = current.getType();
      List<? extends Node> possibilities = doCreateVariable ? variableSet.getByType(type) : constantSet.getByType(type);
      return randomlySelectAlternative(current, possibilities);
   }

   @Override
   public FunctionSet.Key nextFunction(Type type) {
      List<FunctionSet.Key> typeFunctions = functionSet.getByType(type);
      if (typeFunctions == null || typeFunctions.isEmpty()) {
         throw new IllegalArgumentException("No functions with return type: " + type + " Available return types of function set: " + functionSet);
      }
      int index = nextInt(typeFunctions.size());
      return typeFunctions.get(index);
   }

   @Override
   public FunctionSet.Key nextAlternativeFunction(FunctionNode current) {
      ChildNodes children = current.getChildren();
      Type[] types = new Type[children.size()];
      for (int i = 0; i < children.size(); i++) {
         types[i] = children.getNode(i).getType();
      }
      Signature signature = Signature.createSignature(current.getType(), types);
      List<FunctionSet.Key> functions = functionSet.getBySignature(signature);
      if (functions == null) {
         throw new RuntimeException("No functions with signature: " + signature + " to be alternative to: " + current);
      }
      return randomlySelectAlternative(new FunctionSet.Key(current.getFunction(), signature), functions);
   }

   private <C, P extends C> C randomlySelectAlternative(C currentVersion, List<P> possibilities) {
      if (possibilities.isEmpty()) {
         return currentVersion;
      }

      int possibilitiesSize = possibilities.size();
      int randomIndex = nextInt(possibilitiesSize);
      C next = possibilities.get(randomIndex);
      if (next == currentVersion) {
         if (possibilitiesSize == 1) {
            return currentVersion;
         }

         int secondRandomIndex = nextInt(possibilitiesSize - 1);
         if (secondRandomIndex >= randomIndex) {
            return possibilities.get(secondRandomIndex + 1);
         } else {
            return possibilities.get(secondRandomIndex);
         }
      } else {
         return next;
      }
   }

   private int nextInt(int bound) {
      return bound == 1 ? 0 : random.nextInt(bound);
   }

   public PrimitiveSetImpl addFunction(AutomaticallyDefinedFunction newFunction) {
      Collection<FunctionSet.Key> functions = new ArrayList<>(functionSet.getFunctions());
      functions.add(new FunctionSet.Key(newFunction, newFunction.getSignature()));
      return new PrimitiveSetImpl(new FunctionSet(functions), constantSet, variableSet, random, ratioVariables);
   }

   public PrimitiveSetImpl addVariable(String name, Type newVariable) {
      VariableNode[] variables = new VariableNode[variableSet.size() + 1];
      for (int i = 0; i < variableSet.size(); i++) {
         variables[i] = variableSet.getById(i);
      }
      variables[variableSet.size()] = new VariableNode(variableSet.size(), name, newVariable);
      return new PrimitiveSetImpl(functionSet, constantSet, VariableSet.createVariableSet(variables), random, ratioVariables);
   }
}
