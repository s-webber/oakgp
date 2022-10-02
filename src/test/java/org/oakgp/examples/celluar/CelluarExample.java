package org.oakgp.examples.celluar;

import static org.oakgp.examples.celluar.CelluarFitnessFunction.NUM_BITS;
import static org.oakgp.type.CommonTypes.booleanType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import org.oakgp.serialize.NodeReader;
import org.oakgp.util.ConstantSetBuilder;
import org.oakgp.util.FunctionSetBuilder;
import org.oakgp.util.RunBuilder;
import org.oakgp.util.Utils;
import org.oakgp.util.VariableSetBuilder;

public class CelluarExample {
   private static final int INITIAL_POPULATION_SIZE = 1000;
   private static final int INITIAL_POPULATION_MAX_DEPTH = 4;

   public static void main(String[] args) throws IOException {
      FunctionSet functionSet = new FunctionSetBuilder() //
            .add(new If(), booleanType()) //
            .addAll(IsFalse.getSingleton(), And.getSingleton(), Or.getSingleton(), Xor.getSingleton()) //
            .build();
      VariableSet variableSet = new VariableSetBuilder().add(booleanType(), 7).build();

      List<CelluarFitnessFunction.TestCase> testCases = new ArrayList<>();
      Random r = new Random();
      for (int i = 0; i < 10000; i++) {
         int countTrue = 0;
         boolean[] input = new boolean[NUM_BITS];
         for (int b = 0; b < NUM_BITS; b++) {
            input[b] = r.nextBoolean();
            if (input[b]) {
               countTrue++;
            }
         }
         boolean expected = countTrue > NUM_BITS / 2;
         testCases.add(new CelluarFitnessFunction.TestCase(input, expected));
         // System.out.println(testCases.size() + " " + expected + " " + Arrays.toString(input));
      }

      FitnessFunction fitnessFunction = new CelluarFitnessFunction(testCases);

      NodeReader nr = new NodeReader("(xor v2 v0)", functionSet, new ConstantSetBuilder().build(), variableSet);
      long now = System.currentTimeMillis();
      fitnessFunction.evaluate(nr.readNode());
      System.out.println(System.currentTimeMillis() - now);
      // System.exit(-1);

      RankedCandidates output = new RunBuilder() //
            .setReturnType(booleanType()) //
            .setConstants(Utils.TRUE_NODE) //
            .setVariableSet(variableSet) //
            .setFunctionSet(functionSet) //
            .setFitnessFunction(fitnessFunction) //
            .setInitialPopulationSize(INITIAL_POPULATION_SIZE) //
            .setTreeDepth(INITIAL_POPULATION_MAX_DEPTH) //
            .setMaxGenerations(1000) //
            .process();
      Node best = output.best().getNode();
      System.out.println(best);
   }
}
