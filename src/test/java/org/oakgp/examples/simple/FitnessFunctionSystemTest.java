package org.oakgp.examples.simple;

import static org.oakgp.Assignments.createAssignments;
import static org.oakgp.TestUtils.createArguments;
import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerArrayType;
import static org.oakgp.Type.integerToBooleanFunctionType;
import static org.oakgp.Type.integerType;
import static org.oakgp.fitness.TestDataFitnessFunction.createIntegerTestDataFitnessFunction;
import static org.oakgp.util.Utils.createIntegerConstants;
import static org.oakgp.util.Utils.createIntegerTypeArray;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Type;
import org.oakgp.fitness.FitnessFunction;
import org.oakgp.function.Function;
import org.oakgp.function.choice.If;
import org.oakgp.function.classify.IsNegative;
import org.oakgp.function.classify.IsPositive;
import org.oakgp.function.classify.IsZero;
import org.oakgp.function.coll.Count;
import org.oakgp.function.compare.Equal;
import org.oakgp.function.compare.GreaterThan;
import org.oakgp.function.compare.GreaterThanOrEqual;
import org.oakgp.function.compare.LessThan;
import org.oakgp.function.compare.LessThanOrEqual;
import org.oakgp.function.compare.NotEqual;
import org.oakgp.function.hof.Filter;
import org.oakgp.function.math.IntegerUtils;
import org.oakgp.node.ConstantNode;
import org.oakgp.util.RunBuilder;

/**
 * Performs full genetic programming runs without relying on any mock objects.
 * <p>
 * Would be better to have in a separate "system-test" directory under the "src" directory - or in a completely separate Git project (that has this project as a
 * dependency). Leaving here for the moment as it provides a convenient mechanism to perform a full test of the process. TODO decide long-term solution for this
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
      ConstantNode[] constants = createIntegerConstants(0, 11);
      Type[] variableTypes = createIntegerTypeArray(2);

      FitnessFunction fitnessFunction = createIntegerTestDataFitnessFunction(createTests(variableTypes.length, a -> {
         int x = (int) a.get(0);
         int y = (int) a.get(1);
         return (x * x) + 2 * y + 3 * x + 5;
      }));

      new RunBuilder().setReturnType(integerType()).setConstants(constants).setVariables(variableTypes).setFunctionSet(ARITHMETIC_FUNCTIONS)
            .setFitnessFunction(fitnessFunction).setInitialPopulationSize(INITIAL_POPULATION_SIZE).setTreeDepth(INITIAL_POPULATION_MAX_DEPTH)
            .setMaxGenerations(NUM_GENERATIONS).process();
   }

   @Test
   public void testThreeVariableArithmeticExpression() {
      ConstantNode[] constants = createIntegerConstants(0, 11);
      Type[] variableTypes = createIntegerTypeArray(3);

      FitnessFunction fitnessFunction = createIntegerTestDataFitnessFunction(createTests(variableTypes.length, a -> {
         int x = (int) a.get(0);
         int y = (int) a.get(1);
         int z = (int) a.get(2);
         return (x * -3) + (y * 5) - z;
      }));

      new RunBuilder().setReturnType(integerType()).setConstants(constants).setVariables(variableTypes).setFunctionSet(ARITHMETIC_FUNCTIONS)
            .setFitnessFunction(fitnessFunction).setInitialPopulationSize(INITIAL_POPULATION_SIZE).setTreeDepth(INITIAL_POPULATION_MAX_DEPTH)
            .setMaxGenerations(NUM_GENERATIONS).process();
   }

   @Test
   public void testTwoVariableBooleanLogicExpression() {
      ConstantNode[] constants = createIntegerConstants(0, 5);
      Type[] variableTypes = createIntegerTypeArray(2);
      Function[] functions = { IntegerUtils.INTEGER_UTILS.getAdd(), IntegerUtils.INTEGER_UTILS.getSubtract(), IntegerUtils.INTEGER_UTILS.getMultiply(),
            new LessThan(integerType()), new LessThanOrEqual(integerType()), new GreaterThan(integerType()), new GreaterThanOrEqual(integerType()),
            new Equal(integerType()), new NotEqual(integerType()), new If(integerType()) };

      FitnessFunction fitnessFunction = createIntegerTestDataFitnessFunction(createTests(variableTypes.length, a -> {
         int x = (int) a.get(0);
         int y = (int) a.get(1);
         return x > 20 ? x : y;
      }));

      new RunBuilder().setReturnType(integerType()).setConstants(constants).setVariables(variableTypes).setFunctionSet(ARITHMETIC_FUNCTIONS)
            .setFitnessFunction(fitnessFunction).setInitialPopulationSize(INITIAL_POPULATION_SIZE).setTreeDepth(INITIAL_POPULATION_MAX_DEPTH)
            .setMaxGenerations(NUM_GENERATIONS).process();
   }

   @Test
   public void testIsCountOfZerosGreater() {
      IsPositive isPositive = new IsPositive();
      IsNegative isNegative = new IsNegative();
      IsZero isZero = new IsZero();
      ConstantNode[] constants = { new ConstantNode(Boolean.TRUE, booleanType()), new ConstantNode(Boolean.FALSE, booleanType()),
            new ConstantNode(isPositive, integerToBooleanFunctionType()), new ConstantNode(isNegative, integerToBooleanFunctionType()),
            new ConstantNode(isZero, integerToBooleanFunctionType()), new ConstantNode(Arguments.createArguments(), integerArrayType()),
            new ConstantNode(0, integerType()) };
      Type[] variableTypes = { integerArrayType() };
      Function[] functions = { new Filter(integerType()), isPositive, isNegative, isZero, new Count(integerType()) };

      Map<Assignments, Integer> testData = new HashMap<>();
      testData.put(createAssignments(createArguments("0", "0", "0", "0", "0", "0", "0", "0")), 8);
      testData.put(createAssignments(createArguments("6", "3", "4", "0", "2", "4", "1", "3")), 1);
      testData.put(createAssignments(createArguments("0", "0", "4", "0", "0", "0", "1", "0")), 6);
      testData.put(createAssignments(createArguments("1", "-1", "2", "5", "4", "-2")), 0);
      testData.put(createAssignments(createArguments("1", "0", "2", "5", "4", "-2")), 1);
      testData.put(createAssignments(createArguments("1", "0", "2", "5", "4", "0")), 2);
      testData.put(createAssignments(createArguments("-2", "0", "8", "7", "0", "-3", "0")), 3);
      testData.put(createAssignments(createArguments("0", "0", "0")), 3);
      FitnessFunction fitnessFunction = createIntegerTestDataFitnessFunction(testData);

      new RunBuilder().setReturnType(integerType()).setConstants(constants).setVariables(variableTypes).setFunctionSet(ARITHMETIC_FUNCTIONS)
            .setFitnessFunction(fitnessFunction).setInitialPopulationSize(INITIAL_POPULATION_SIZE).setTreeDepth(INITIAL_POPULATION_MAX_DEPTH)
            .setMaxGenerations(NUM_GENERATIONS).process();
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
