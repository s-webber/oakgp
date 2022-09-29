package org.oakgp.examples.celluar;

import static org.oakgp.type.CommonTypes.booleanType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.oakgp.Assignments;
import org.oakgp.function.bool.And;
import org.oakgp.function.bool.Or;
import org.oakgp.function.bool.Xor;
import org.oakgp.function.choice.If;
import org.oakgp.function.classify.IsFalse;
import org.oakgp.node.Node;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.primitive.VariableSet;
import org.oakgp.rank.RankedCandidates;
import org.oakgp.rank.fitness.FitnessFunction;
import org.oakgp.util.FunctionSetBuilder;
import org.oakgp.util.RunBuilder;
import org.oakgp.util.Utils;
import org.oakgp.util.VariableSetBuilder;

public class CelluarExample {
   private static final int NUM_BITS = 149; // TODO share with CelluarFitnessFunction
   private static final int INITIAL_POPULATION_SIZE = 50;
   private static final int INITIAL_POPULATION_MAX_DEPTH = 4;

   public static void main(String[] args) {
      FunctionSet functionSet = new FunctionSetBuilder() //
            .add(new If(), booleanType()) //
            .addAll(IsFalse.getSingleton(), And.getSingleton(), Or.getSingleton(), Xor.getSingleton()) //
            .build();
      VariableSet variableSet = new VariableSetBuilder().add(booleanType(), 7).build();

      Map<Assignments, Boolean> testCases = new HashMap<>();
      Random r = new Random();
      for (int i = 0; i < 10000; i++) {
         int countTrue = 0;
         Object[] input = new Object[NUM_BITS];
         for (int b = 0; b < NUM_BITS; b++) {
            input[b] = r.nextBoolean();
            if ((Boolean) input[b]) {
               countTrue++;
            }
         }
         boolean expected = countTrue > NUM_BITS / 2;
         testCases.put(Assignments.createAssignments(input), expected);
         // System.out.println(testCases.size() + " " + expected + " " + Arrays.toString(input));
      }

      FitnessFunction fitnessFunction = new CelluarFitnessFunction(testCases);

      RankedCandidates output = new RunBuilder() //
            .setReturnType(booleanType()) //
            .setConstants(Utils.TRUE_NODE) //
            .setVariableSet(variableSet) //
            .setFunctionSet(functionSet) //
            .setFitnessFunction(fitnessFunction) //
            .setInitialPopulationSize(INITIAL_POPULATION_SIZE) //
            .setTreeDepth(INITIAL_POPULATION_MAX_DEPTH) //
            .setMaxGenerations(100) //
            .process();
      Node best = output.best().getNode();
      System.out.println(best);
   }
}
