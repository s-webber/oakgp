package org.oakgp.examples.celluar;

import java.util.Arrays;
import java.util.List;

import org.oakgp.Assignments;
import org.oakgp.node.Node;
import org.oakgp.rank.fitness.FitnessFunction;

final class CelluarFitnessFunction implements FitnessFunction {
   static final int NUM_BITS = 149;
   private static final int NUM_ITERATIONS = 600;
   // there are 128 possible combinations of 7 booleans
   private static final Assignments[] ASSIGNMENTS = new Assignments[128];
   static {
      for (int i = 0; i < ASSIGNMENTS.length; i++) {
         boolean b1 = (i & 0b1) != 0;
         boolean b2 = (i & 0b10) != 0;
         boolean b3 = (i & 0b100) != 0;
         boolean b4 = (i & 0b1000) != 0;
         boolean b5 = (i & 0b10000) != 0;
         boolean b6 = (i & 0b100000) != 0;
         boolean b7 = (i & 0b1000000) != 0;
         ASSIGNMENTS[i] = Assignments.createAssignments(b1, b2, b3, b4, b5, b6, b7);
      }
   }

   private final List<TestCase> testCases;

   CelluarFitnessFunction(List<TestCase> testCases) {
      this.testCases = testCases;
   }

   @Override
   public double evaluate(Node candidate) {
      int fitness = testCases.size();

      boolean[] cache = createCache(candidate);

      for (TestCase testCase : testCases) {
         Boolean output = evaluateTestCase(testCase.input, cache);

         if (output != null && testCase.expected == output.booleanValue()) {
            fitness--;
         }
      }

      return fitness;
   }

   private boolean[] createCache(Node candidate) {
      boolean[] cache = new boolean[ASSIGNMENTS.length];
      for (int i = 0; i < cache.length; i++) {
         cache[i] = candidate.evaluate(ASSIGNMENTS[i], null);
      }
      return cache;
   }

   private Boolean evaluateTestCase(boolean[] input, boolean[] cache) {
      boolean[] current = input;

      for (int i = 0; i < NUM_ITERATIONS; i++) {
         boolean[] next = nextIteration(current, cache);
         if (Arrays.equals(current, next)) {
            break;
         }
         current = next;
      }

      return getOutcome(current);
   }

   private boolean[] nextIteration(boolean[] input, boolean cache[]) {
      boolean[] result = new boolean[NUM_BITS];

      int previous = (input[NUM_BITS - 3] ? 1 : 0) + (input[NUM_BITS - 2] ? 2 : 0) + (input[NUM_BITS - 1] ? 4 : 0) + (input[0] ? 8 : 0) + (input[1] ? 16 : 0)
            + (input[2] ? 32 : 0) + (input[3] ? 64 : 0);
      for (int i = 0; i < NUM_BITS; i++) {
         result[i] = cache[previous];
         previous = input[i < NUM_BITS - 3 ? i + 3 : i - (NUM_BITS - 3)] ? (previous >> 1) + 64 : (previous >> 1);
      }

      return result;
   }

   private Boolean getOutcome(boolean[] arguments) {
      boolean outcome = arguments[0];

      for (int i = 1; i < NUM_BITS; i++) {
         if (arguments[i] != outcome) {
            return null;
         }
      }

      return outcome;
   }

   static final class TestCase {
      private final boolean[] input;
      private final boolean expected;

      TestCase(boolean[] input, boolean expected) {
         this.input = input;
         this.expected = expected;
      }
   }
}
