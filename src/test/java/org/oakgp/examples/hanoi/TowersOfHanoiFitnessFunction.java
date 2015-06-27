package org.oakgp.examples.hanoi;

import java.util.HashSet;
import java.util.Set;

import org.oakgp.Assignments;
import org.oakgp.fitness.FitnessFunction;
import org.oakgp.node.Node;

/** Determines the fitness of potential solutions to the Towers of Hanoi puzzle. */
class TowersOfHanoiFitnessFunction implements FitnessFunction {
   private static final TowersOfHanoi START_STATE = new TowersOfHanoi();

   private final boolean doLog;

   TowersOfHanoiFitnessFunction(boolean doLog) {
      this.doLog = doLog;
   }

   /**
    * @param n
    *           a potential solution to the Towers of Hanoi puzzle
    * @return the fitness of {@code n}
    */
   @Override
   public double evaluate(Node n) {
      TowersOfHanoi towersOfHanoi = START_STATE;
      Set<TowersOfHanoi> previousStates = new HashSet<>();
      previousStates.add(towersOfHanoi);

      Move previousMove = null;
      int previousFitness = Integer.MAX_VALUE;
      while (true) {
         // update the puzzle with the next move returned from the potential solution
         Assignments assignments = Assignments.createAssignments(towersOfHanoi, previousMove);
         previousMove = n.evaluate(assignments);
         towersOfHanoi = towersOfHanoi.move(previousMove);
         if (doLog) {
            System.out.println(previousMove + " " + towersOfHanoi);
         }

         if (towersOfHanoi == null) {
            // the last move was invalid - stop evaluating
            return previousFitness;
         }
         if (!previousStates.add(towersOfHanoi)) {
            // the last move has returned the puzzle to a state it has already been in - exit now to avoid getting stuck in a loop
            return previousFitness;
         }
         previousFitness = Math.min(previousFitness, towersOfHanoi.getFitness());
         if (previousFitness == 0) {
            // the puzzle has been solved - no need to keep evaluating
            return previousFitness;
         }
      }
   }
}
