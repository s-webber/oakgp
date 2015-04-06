package org.oakgp.fitness;

import java.util.Map;

import org.oakgp.Assignments;
import org.oakgp.node.Node;

/** Calculates the fitness of a potential solution by comparing its results against the expected values. */
public final class TestDataFitnessFunction implements FitnessFunction {
   private final Map<Assignments, Integer> tests;

   /**
    * Constructs a new {@code FitnessFunction} which uses the specified test data to assess the fitness of potential solutions.
    *
    * @param tests
    *           test data which associates a collection of inputs with their expected outcomes
    */
   public TestDataFitnessFunction(Map<Assignments, Integer> tests) {
      this.tests = tests;
   }

   /**
    * Evaluates the specified {@code Node} using the test data specified when this {@code FitnessFunction} was constructed.
    *
    * @param node
    *           the potential solution that whose fitness will be determined
    * @return the accumulative difference between the expected and actual outputs of evaluating {@code node} using each of the inputs of the test data
    */
   @Override
   public double evaluate(Node node) {
      double diff = 0;
      for (Map.Entry<Assignments, Integer> test : tests.entrySet()) {
         Assignments input = test.getKey();
         int expected = test.getValue();
         // TODO in future, the result of node.evaluate(input) might not always be an int
         // TODO allow a function to be specified in constructor which converts the result of evaluate to an int
         int actual = (int) node.evaluate(input);
         diff += Math.abs(actual - expected);
      }
      return diff;
   }
}
