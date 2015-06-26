package org.oakgp.examples.hanoi;

import java.util.HashSet;
import java.util.Set;

import org.oakgp.Assignments;
import org.oakgp.fitness.FitnessFunction;
import org.oakgp.node.Node;

class TowersOfHanoiFitnessFunction implements FitnessFunction {
   private static final TowersOfHanoi START_STATE = new TowersOfHanoi();

   private final boolean doLog;

   TowersOfHanoiFitnessFunction(boolean doLog) {
      this.doLog = doLog;
   }

   @Override
   public double evaluate(Node n) {
      TowersOfHanoi towersOfHanoi = START_STATE;
      Set<TowersOfHanoi> previousStates = new HashSet<>();
      previousStates.add(towersOfHanoi);

      Move previousMove = null;
      int previousFitness = Integer.MAX_VALUE;
      while (true) {
         Assignments assignments = Assignments.createAssignments(towersOfHanoi, previousMove);
         previousMove = n.evaluate(assignments);
         towersOfHanoi = towersOfHanoi.move(previousMove);
         if (doLog) {
            System.out.println(previousMove + " " + towersOfHanoi);
         }
         if (towersOfHanoi == null || !previousStates.add(towersOfHanoi)) {
            return previousFitness;
         }
         previousFitness = Math.min(previousFitness, towersOfHanoi.getFitness());
         if (previousFitness == 0) {
            return previousFitness;
         }
      }
   }
}
