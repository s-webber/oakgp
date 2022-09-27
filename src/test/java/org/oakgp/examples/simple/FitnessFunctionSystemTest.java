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
package org.oakgp.examples.simple;

import static java.util.Arrays.asList;
import static org.oakgp.Assignments.createAssignments;
import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.integerListType;
import static org.oakgp.type.CommonTypes.integerToBooleanFunctionType;
import static org.oakgp.type.CommonTypes.integerType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.choice.If;
import org.oakgp.function.classify.IsNegative;
import org.oakgp.function.classify.IsPositive;
import org.oakgp.function.classify.IsZero;
import org.oakgp.function.coll.CountList;
import org.oakgp.function.compare.Equal;
import org.oakgp.function.compare.GreaterThan;
import org.oakgp.function.compare.GreaterThanOrEqual;
import org.oakgp.function.compare.NotEqual;
import org.oakgp.function.hof.Filter;
import org.oakgp.function.math.IntegerUtils;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.primitive.ConstantSet;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.primitive.VariableSet;
import org.oakgp.rank.RankedCandidates;
import org.oakgp.rank.fitness.TestDataFitnessFunction;
import org.oakgp.type.Types.Type;
import org.oakgp.util.ConstantSetBuilder;
import org.oakgp.util.FunctionSetBuilder;
import org.oakgp.util.RunBuilder;

/**
 * Performs full genetic programming runs without relying on any mock objects.
 * <p>
 * Would be better to have in a separate "system-test" directory under the "src" directory - or in a completely separate Git project (that has this project as a
 * dependency). Leaving here for the moment as it provides a convenient mechanism to perform a full test of the process.
 * </p>
 */
public class FitnessFunctionSystemTest {
   private static final int NUM_GENERATIONS = 50;
   private static final int INITIAL_POPULATION_SIZE = 50;
   private static final int INITIAL_POPULATION_MAX_DEPTH = 4;
   private static final Function[] ARITHMETIC_FUNCTIONS = { IntegerUtils.INTEGER_UTILS.getAdd(), IntegerUtils.INTEGER_UTILS.getSubtract(),
         IntegerUtils.INTEGER_UTILS.getMultiply() };

   @Test
   public void testSymbolicRegressionExample() {
      SymbolicRegressionExample.main(null);
   }

   @Test
   public void testTwoVariableArithmeticExpression() {
      ConstantSet constantSet = new ConstantSetBuilder().integerRange(0, 11).build();
      VariableSet variableSet = VariableSet.createVariableSet(integerType(), integerType());
      TestDataFitnessFunction<Integer> fitnessFunction = new TestDataFitnessFunction<>(createTests(variableSet.size(), a -> {
         int x = (int) a.get(0);
         int y = (int) a.get(1);
         return (x * x) + 2 * y + 3 * x + 5;
      }), (e, a) -> Math.abs(e - a));

      RankedCandidates output = new RunBuilder() //
            .setReturnType(integerType()) //
            .setConstants(constantSet) //
            .setVariables(variableSet) //
            .setFunctions(ARITHMETIC_FUNCTIONS) //
            .setFitnessFunction(fitnessFunction) //
            .setInitialPopulationSize(INITIAL_POPULATION_SIZE) //
            .setTreeDepth(INITIAL_POPULATION_MAX_DEPTH) //
            .setMaxGenerations(NUM_GENERATIONS) //
            .process();
      Node best = output.best().getNode();
      System.out.println(best);
      fitnessFunction.evaluate(best, System.out::println);
   }

   @Test
   public void testThreeVariableArithmeticExpression() {
      ConstantSet constantSet = new ConstantSetBuilder().integerRange(0, 11).build();
      VariableSet variableSet = VariableSet.createVariableSet(integerType(), integerType(), integerType());
      TestDataFitnessFunction<Integer> fitnessFunction = new TestDataFitnessFunction<>(createTests(variableSet.size(), a -> {
         int x = (int) a.get(0);
         int y = (int) a.get(1);
         int z = (int) a.get(2);
         return (x * -3) + (y * 5) - z;
      }), (e, a) -> Math.abs(e - a));

      RankedCandidates output = new RunBuilder() //
            .setReturnType(integerType()) //
            .setConstants(constantSet) //
            .setVariables(variableSet) //
            .setFunctions(ARITHMETIC_FUNCTIONS) //
            .setFitnessFunction(fitnessFunction) //
            .setInitialPopulationSize(INITIAL_POPULATION_SIZE) //
            .setTreeDepth(INITIAL_POPULATION_MAX_DEPTH) //
            .setMaxGenerations(NUM_GENERATIONS) //
            .process();
      Node best = output.best().getNode();
      System.out.println(best);
      fitnessFunction.evaluate(best, System.out::println);
   }

   @Test
   public void testTwoVariableBooleanLogicExpression() {
      FunctionSet functionSet = new FunctionSetBuilder()
            .addAll(IntegerUtils.INTEGER_UTILS.getAdd(), IntegerUtils.INTEGER_UTILS.getSubtract(), IntegerUtils.INTEGER_UTILS.getMultiply()) //
            .add(GreaterThan.getSingleton(), integerType()) //
            .add(GreaterThanOrEqual.getSingleton(), integerType()) //
            .add(Equal.getSingleton(), integerType()) //
            .add(NotEqual.getSingleton(), integerType()) //
            .add(new If(), integerType()) //
            .build();
      ConstantSet constantSet = new ConstantSetBuilder().integerRange(0, 5).build();
      VariableSet variableSet = VariableSet.createVariableSet(integerType(), integerType());
      TestDataFitnessFunction<Integer> fitnessFunction = new TestDataFitnessFunction<>(createTests(variableSet.size(), a -> {
         int x = (int) a.get(0);
         int y = (int) a.get(1);
         return x > 20 ? x : y;
      }), (e, a) -> Math.abs(e - a));

      RankedCandidates output = new RunBuilder() //
            .setReturnType(integerType()) //
            .setConstants(constantSet) //
            .setVariables(variableSet) //
            .setFunctionSet(functionSet) //
            .setFitnessFunction(fitnessFunction) //
            .setInitialPopulationSize(INITIAL_POPULATION_SIZE) //
            .setTreeDepth(INITIAL_POPULATION_MAX_DEPTH) //
            .setMaxGenerations(NUM_GENERATIONS) //
            .process();
      Node best = output.best().getNode();
      System.out.println(best);
      fitnessFunction.evaluate(best, System.out::println);
   }

   @Test
   public void testCountZeros() {
      IsPositive isPositive = IsPositive.getSingleton();
      IsNegative isNegative = IsNegative.getSingleton();
      IsZero isZero = IsZero.getSingleton();
      Filter filter = new Filter();
      CountList count = new CountList();
      ConstantNode[] constants = { new ConstantNode(Boolean.TRUE, booleanType()), new ConstantNode(Boolean.FALSE, booleanType()),
            new ConstantNode(isPositive, integerToBooleanFunctionType()), new ConstantNode(isNegative, integerToBooleanFunctionType()),
            new ConstantNode(isZero, integerToBooleanFunctionType()), new ConstantNode(Collections.emptyList(), integerListType()),
            new ConstantNode(0, integerType()) };
      Type[] variableTypes = { integerListType() };
      FunctionSet functionSet = new FunctionSetBuilder().addAll(ARITHMETIC_FUNCTIONS).addAll(isPositive, isNegative, isZero).add(filter, integerType())
            .add(count, integerType()).build();

      Map<Assignments, Integer> testData = new HashMap<>();
      testData.put(createAssignments(asList(0, 0, 0, 0, 0, 0, 0, 0)), 8);
      testData.put(createAssignments(asList(6, 3, 4, 0, 2, 4, 1, 3)), 1);
      testData.put(createAssignments(asList(0, 0, 4, 0, 0, 0, 1, 0)), 6);
      testData.put(createAssignments(asList(1, -1, 2, 5, 4, -2)), 0);
      testData.put(createAssignments(asList(1, 0, 2, 5, 4, -2)), 1);
      testData.put(createAssignments(asList(1, 0, 2, 5, 4, 0)), 2);
      testData.put(createAssignments(asList(-2, 0, 8, 7, 0, -3, 0)), 3);
      testData.put(createAssignments(asList(0, 0, 0)), 3);
      TestDataFitnessFunction<Integer> fitnessFunction = new TestDataFitnessFunction<>(testData, (e, a) -> Math.abs(e - a));

      RankedCandidates output = new RunBuilder().setReturnType(integerType()).setConstants(constants).setVariables(variableTypes).setFunctionSet(functionSet)
            .setFitnessFunction(fitnessFunction).setInitialPopulationSize(INITIAL_POPULATION_SIZE).setTreeDepth(INITIAL_POPULATION_MAX_DEPTH)
            .setMaxGenerations(NUM_GENERATIONS).process();
      Node best = output.best().getNode();
      System.out.println(best);
      fitnessFunction.evaluate(best, System.out::println);
   }

   private static Map<Assignments, Integer> createTests(int numVariables, java.util.function.Function<Assignments, Integer> f) {
      Map<Assignments, Integer> tests = new HashMap<>();
      for (int i = 0; i < 200; i++) {
         Object[] inputs = createInputs(numVariables);
         Assignments assignments = createAssignments(inputs);
         tests.put(assignments, f.apply(assignments));
      }
      return tests;
   }

   private static Object[] createInputs(int numVariables) {
      Random random = new Random();
      Object[] variables = new Object[numVariables];
      for (int i = 0; i < numVariables; i++) {
         variables[i] = random.nextInt(40);
      }
      return variables;
   }
}
