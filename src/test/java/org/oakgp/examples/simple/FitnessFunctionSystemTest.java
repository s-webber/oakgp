package org.oakgp.examples.simple;

import static org.oakgp.Assignments.createAssignments;
import static org.oakgp.TestUtils.createArguments;
import static org.oakgp.TestUtils.createTypeArray;
import static org.oakgp.Type.arrayType;
import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.functionType;
import static org.oakgp.Type.integerType;
import static org.oakgp.examples.SystemTestUtils.ARITHMETIC_FUNCTION_SET;
import static org.oakgp.examples.SystemTestUtils.COMPARISON_FUNCTION_SET;
import static org.oakgp.examples.SystemTestUtils.ELITISM_SIZE;
import static org.oakgp.examples.SystemTestUtils.GENERATION_SIZE;
import static org.oakgp.examples.SystemTestUtils.RANDOM;
import static org.oakgp.examples.SystemTestUtils.RATIO_VARIABLES;
import static org.oakgp.examples.SystemTestUtils.SELECTOR_FACTORY;
import static org.oakgp.examples.SystemTestUtils.createConstants;
import static org.oakgp.examples.SystemTestUtils.createInitialGeneration;
import static org.oakgp.examples.SystemTestUtils.makeRandomTree;
import static org.oakgp.examples.SystemTestUtils.printRankedCandidate;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.Test;
import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.FunctionSet;
import org.oakgp.GenerationEvolver;
import org.oakgp.GenerationProcessor;
import org.oakgp.NodeEvolver;
import org.oakgp.RankedCandidate;
import org.oakgp.Runner;
import org.oakgp.Signature;
import org.oakgp.TerminalSet;
import org.oakgp.Type;
import org.oakgp.crossover.SubtreeCrossover;
import org.oakgp.fitness.FitnessFunction;
import org.oakgp.fitness.FitnessFunctionCache;
import org.oakgp.fitness.FitnessFunctionGenerationProcessor;
import org.oakgp.fitness.TestDataFitnessFunction;
import org.oakgp.function.Function;
import org.oakgp.function.classify.IsNegative;
import org.oakgp.function.classify.IsPositive;
import org.oakgp.function.classify.IsZero;
import org.oakgp.function.coll.Count;
import org.oakgp.function.hof.Filter;
import org.oakgp.mutate.PointMutation;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;

/**
 * Performs full genetic programming runs without relying on any mock objects.
 * <p>
 * Would be better to have in a separate "system-test" directory under the "src" directory - or in a completely separate Git project (that has this project as a
 * dependency). Leaving here for the moment as it provides a convenient mechanism to perform a full test of the process. TODO decide long-term solution for this
 * </p>
 */
public class FitnessFunctionSystemTest {
   @Test
   public void testTwoVariableArithmeticExpression() {
      ConstantNode[] constants = createConstants(11);
      int numVariables = 2;
      TerminalSet terminalSet = new TerminalSet(RANDOM, RATIO_VARIABLES, createTypeArray(numVariables), constants);
      FitnessFunction fitnessFunction = new TestDataFitnessFunction(createTests(numVariables, a -> {
         int x = (int) a.get(0);
         int y = (int) a.get(1);
         return (x * x) + 2 * y + 3 * x + 5;
      }));
      Collection<Node> initialGeneration = createInitialGeneration(ARITHMETIC_FUNCTION_SET, terminalSet, GENERATION_SIZE);
      doIt(ARITHMETIC_FUNCTION_SET, terminalSet, fitnessFunction, initialGeneration);
   }

   @Test
   public void testThreeVariableArithmeticExpression() {
      ConstantNode[] constants = createConstants(11);
      int numVariables = 3;
      TerminalSet terminalSet = new TerminalSet(RANDOM, RATIO_VARIABLES, createTypeArray(numVariables), constants);
      FitnessFunction fitnessFunction = new TestDataFitnessFunction(createTests(numVariables, a -> {
         int x = (int) a.get(0);
         int y = (int) a.get(1);
         int z = (int) a.get(2);
         return (x * -3) + (y * 5) - z;
      }));
      Collection<Node> initialGeneration = createInitialGeneration(ARITHMETIC_FUNCTION_SET, terminalSet, GENERATION_SIZE);
      doIt(ARITHMETIC_FUNCTION_SET, terminalSet, fitnessFunction, initialGeneration);
   }

   @Test
   public void testTwoVariableBooleanLogicExpression() {
      ConstantNode[] constants = createConstants(5);
      int numVariables = 2;
      TerminalSet terminalSet = new TerminalSet(RANDOM, RATIO_VARIABLES, createTypeArray(numVariables), constants);
      FitnessFunction fitnessFunction = new TestDataFitnessFunction(createTests(numVariables, a -> {
         int x = (int) a.get(0);
         int y = (int) a.get(1);
         return x > 20 ? x : y;
      }));
      Collection<Node> initialGeneration = createInitialGeneration(COMPARISON_FUNCTION_SET, terminalSet, GENERATION_SIZE);
      doIt(COMPARISON_FUNCTION_SET, terminalSet, fitnessFunction, initialGeneration);
   }

   @Test
   public void testIsCountOfZerosGreater() {
      IsPositive isPositive = new IsPositive();
      IsNegative isNegative = new IsNegative();
      IsZero isZero = new IsZero();
      // TODO add to TestUtils: operatorConstant(); integerConstant(); trueConstant(); falseConstant(); arrayConstant();
      ConstantNode[] constants = { new ConstantNode(Boolean.TRUE, booleanType()), new ConstantNode(Boolean.FALSE, booleanType()),
            new ConstantNode(isPositive, functionType()), new ConstantNode(isNegative, functionType()), new ConstantNode(isZero, functionType()),
            new ConstantNode(Arguments.createArguments(), arrayType()), new ConstantNode(0, integerType()) };
      TerminalSet terminalSet = new TerminalSet(RANDOM, RATIO_VARIABLES, new Type[] { arrayType() }, constants);
      Map<Assignments, Integer> testData = new HashMap<>();
      testData.put(createAssignments(createArguments("0", "0", "0", "0", "0", "0", "0", "0")), 8);
      testData.put(createAssignments(createArguments("6", "3", "4", "0", "2", "4", "1", "3")), 1);
      testData.put(createAssignments(createArguments("0", "0", "4", "0", "0", "0", "1", "0")), 6);
      testData.put(createAssignments(createArguments("1", "-1", "2", "5", "4", "-2")), 0);
      testData.put(createAssignments(createArguments("1", "0", "2", "5", "4", "-2")), 1);
      testData.put(createAssignments(createArguments("1", "0", "2", "5", "4", "0")), 2);
      testData.put(createAssignments(createArguments("-2", "0", "8", "7", "0", "-3", "0")), 3);
      testData.put(createAssignments(createArguments("0", "0", "0")), 3);
      FitnessFunction fitnessFunction = new TestDataFitnessFunction(testData);
      FunctionSet hofFunctionSet = new FunctionSet(RANDOM, new Function[] { isNegative, isPositive, isZero, new Filter(), new Count(), createIdentity(arrayType()),
            createIdentity(integerType()), createIdentity(functionType()) });
      Collection<Node> initialGeneration = createInitialGeneration(hofFunctionSet, terminalSet, GENERATION_SIZE);
      doIt(hofFunctionSet, terminalSet, fitnessFunction, initialGeneration);
   }

   private Function createIdentity(final Type type) {
      return new Function() {
         @Override
         public Object evaluate(Arguments arguments, Assignments assignments) {
            return arguments.get(0).evaluate(assignments);
         }

         @Override
         public Signature getSignature() {
            return Signature.createSignature(type, type);
         }

         @Override
         public Node simplify(Arguments arguments) {
            return arguments.get(0);
         }
      };
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
      Object[] variables = new Object[numVariables];
      for (int i = 0; i < numVariables; i++) {
         variables[i] = RANDOM.nextInt(40);
      }
      return variables;
   }

   private void doIt(FunctionSet functionSet, TerminalSet terminalSet, FitnessFunction fitnessFunction, Collection<Node> initialGeneration) {
      Predicate<List<RankedCandidate>> terminator = createTerminator();
      Map<NodeEvolver, Long> nodeEvolvers = createNodeEvolvers(functionSet, terminalSet);
      FitnessFunction fitnessFunctionCache = new FitnessFunctionCache(GENERATION_SIZE, fitnessFunction);
      GenerationProcessor generationProcessor = new FitnessFunctionGenerationProcessor(fitnessFunctionCache);
      GenerationEvolver generationEvolver = new GenerationEvolver(ELITISM_SIZE, SELECTOR_FACTORY, nodeEvolvers);
      RankedCandidate best = Runner.process(generationProcessor, generationEvolver, terminator, initialGeneration);
      printRankedCandidate(best);
   }

   private Map<NodeEvolver, Long> createNodeEvolvers(FunctionSet functionSet, TerminalSet terminalSet) {
      Map<NodeEvolver, Long> nodeEvolvers = new HashMap<>();
      nodeEvolvers.put(t -> makeRandomTree(functionSet, terminalSet, 4), 5L);
      nodeEvolvers.put(new SubtreeCrossover(RANDOM), 21L);
      nodeEvolvers.put(new PointMutation(RANDOM, functionSet, terminalSet), 21L);
      return nodeEvolvers;
   }

   private Predicate<List<RankedCandidate>> createTerminator() {
      return new Predicate<List<RankedCandidate>>() {
         int ctr = 0;
         double previousBest = 0;

         @Override
         public boolean test(List<RankedCandidate> t) {
            ctr++;
            double best = t.get(0).getFitness();
            boolean finished = ctr > 500 || best == 0;
            if (previousBest != best) {
               previousBest = best;
               System.out.println(ctr + " " + best);
            } else if (finished || ctr % 100 == 0) {
               System.out.println(ctr + " " + best);
            }
            return finished;
         }
      };
   }
}
