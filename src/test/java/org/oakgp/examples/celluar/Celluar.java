package org.oakgp.examples.celluar;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

import org.oakgp.Assignments;
import org.oakgp.node.AbstractDefinedFunctions;
import org.oakgp.node.Node;
import org.oakgp.rank.fitness.FitnessFunction;
import org.oakgp.util.DummyNode;

class Celluar implements FitnessFunction {
   private static final int NUM_ITERATIONS = 600;
   private static final int NUM_BITS = 149;
   private static final int NUM_ARGS = 7;
   private static final int[][] BITMASKS = new int[NUM_BITS][];
   // there are 128 possible combinations of 7 booleans
   private static final Assignments[] ASSIGNMENTS = new Assignments[128];
   static {
      for (int i = 0; i < NUM_BITS; i++) {
         BITMASKS[i] = createBitmask(i);
         System.out.println(i + " " + Arrays.toString(BITMASKS[i]));
      }

      for (int i = 0; i < ASSIGNMENTS.length; i++) {
         boolean b1 = (i & 0b1) != 0;
         boolean b2 = (i & 0b10) != 0;
         boolean b3 = (i & 0b100) != 0;
         boolean b4 = (i & 0b1000) != 0;
         boolean b5 = (i & 0b10000) != 0;
         boolean b6 = (i & 0b100000) != 0;
         boolean b7 = (i & 0b1000000) != 0;
         ASSIGNMENTS[i] = Assignments.createAssignments(b1, b2, b3, b4, b5, b6, b7);
         System.out.println(i + " " + ASSIGNMENTS[i]);
      }
   }

   private static int[] createBitmask(int idx) {
      int[] params = new int[NUM_ARGS];
      for (int i = 0; i < params.length; i++) {
         int q = idx + i - (NUM_ARGS / 2);
         if (q < 0) {
            q = NUM_BITS + q;
         } else if (q >= NUM_BITS) {
            q = q - NUM_BITS;
         }
         params[i] = q;
      }
      return params;
   }

   public static void main(String[] args) {
      Random r = new Random();
      Object[] input = new Object[NUM_BITS];
      for (int i = 0; i < NUM_BITS; i++) {
         input[i] = r.nextBoolean();
      }

      Map<Assignments, Boolean> m = Collections.singletonMap(Assignments.createAssignments(input), true);
      new Celluar(m).evaluate(new DummyNode() {
         @Override
         public Boolean evaluate(Assignments assignments, AbstractDefinedFunctions adfs) {
            return r.nextBoolean();
         }
      });
   }

   private final Map<Assignments, Boolean> testCases;

   Celluar(Map<Assignments, Boolean> testCases) {
      this.testCases = testCases;
   }

   @Override
   public double evaluate(Node candidate) {
      int fitness = 0;

      Boolean cache[] = new Boolean[ASSIGNMENTS.length];

      for (Map.Entry<Assignments, Boolean> testCase : testCases.entrySet()) {
         boolean[] input = toBooleans(testCase.getKey());
         Boolean output = evaluateTestCase(candidate, input, cache);

         if (testCase.getValue().equals(output)) {
            fitness++;
         }
      }

      return fitness;
   }

   private boolean[] toBooleans(Assignments input) {
      boolean[] output = new boolean[NUM_BITS];

      for (int i = 0; i < NUM_BITS; i++) {
         output[i] = (boolean) input.get(i);
      }

      return output;
   }

   private Boolean evaluateTestCase(Node candidate, boolean[] input, Boolean[] cache) {
      boolean[] current = input;

      for (int i = 0; i < NUM_ITERATIONS; i++) {
         boolean[] next = nextIteration(current, candidate, cache);
         if (Arrays.equals(current, next)) {
            break;
         }
         current = next;
      }

      return getOutcome(current);
   }

   private boolean[] nextIteration(boolean[] input, Node candidate, Boolean cache[]) {
      boolean[] result = new boolean[NUM_BITS];

      for (int i = 0; i < NUM_BITS; i++) {
         int assignmentIdx = calculateAssignmentIdx(input, i);
         Boolean bit = cache[assignmentIdx];
         if (bit == null) {
            bit = candidate.evaluate(ASSIGNMENTS[assignmentIdx], null);
            cache[assignmentIdx] = bit;
         }
         result[i] = bit;
      }

      return result;
   }

   private int calculateAssignmentIdx(boolean[] input, int idx) {
      int assignmentIdx = 0;

      int[] bitmask = BITMASKS[idx];
      for (int i = 0, b = 1; i < NUM_ARGS; i++, b *= 2) {
         if (input[bitmask[i]]) {
            assignmentIdx += b;
         }
      }

      return assignmentIdx;
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
}
