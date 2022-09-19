package org.oakgp.examples.snake;

import org.oakgp.Assignments;
import org.oakgp.node.Node;
import org.oakgp.rank.fitness.FitnessFunction;

class SnakeFitnessFunction implements FitnessFunction {
   @Override
   public double evaluate(Node n) {
      return evaluate(n, false);
   }

   double evaluate(Node n, boolean draw) {
      int fitness = 0;

      for (int i = 0; i < 1; i++) {
         Snake snake = new Snake(draw);
         Assignments assignments = Assignments.createAssignments(snake);

         while (!snake.gameOver()) {
            n.evaluate(assignments);
         }

         fitness += snake.getFitness();
      }

      return fitness;
   }
}
