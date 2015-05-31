package org.oakgp.examples.simple;

import java.util.HashMap;
import java.util.Map;

import org.oakgp.Assignments;
import org.oakgp.Type;
import org.oakgp.examples.RunBuilder;
import org.oakgp.fitness.FitnessFunction;
import org.oakgp.fitness.TestDataFitnessFunction;
import org.oakgp.function.Function;
import org.oakgp.function.math.IntegerUtils;
import org.oakgp.node.ConstantNode;
import org.oakgp.util.Utils;

public class SymbolicRegressionExample {
   private static final int NUM_GENERATIONS = 500;
   private static final int INITIAL_GENERATION_SIZE = 50;
   private static final int INITIAL_GENERATION_MAX_DEPTH = 4;
   private static final Function[] ARITHMETIC_FUNCTIONS = { IntegerUtils.INTEGER_UTILS.getAdd(), IntegerUtils.INTEGER_UTILS.getSubtract(),
         IntegerUtils.INTEGER_UTILS.getMultiply() };

   // x2 + x + 1
   public static void main(String[] args) {
      ConstantNode[] constants = Utils.createIntegerConstants(0, 10);
      Type[] variableTypes = { Type.integerType() };
      FitnessFunction fitnessFunction = TestDataFitnessFunction.createIntegerTestDataFitnessFunction(createTests());

      new RunBuilder().setReturnType(Type.integerType()).useDefaultRandom().setFunctionSet(ARITHMETIC_FUNCTIONS).setConstants(constants)
            .setVariables(variableTypes).setFitnessFunction(fitnessFunction).useDefaultGenerationEvolver().setMaxGenerations(NUM_GENERATIONS)
            .setInitialGenerationSize(INITIAL_GENERATION_SIZE).setTreeDepth(INITIAL_GENERATION_MAX_DEPTH).process();
   }

   private static Map<Assignments, Integer> createTests() {
      Map<Assignments, Integer> tests = new HashMap<>();
      for (int i = -10; i < 11; i++) {
         Assignments assignments = Assignments.createAssignments(i);
         tests.put(assignments, (i * i) + i + 1);
      }
      return tests;
   }
}
