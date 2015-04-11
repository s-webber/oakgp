package org.oakgp.examples.simple;

import static org.oakgp.Assignments.createAssignments;
import static org.oakgp.TestUtils.createArguments;
import static org.oakgp.TestUtils.createTypeArray;
import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerArrayType;
import static org.oakgp.Type.integerToBooleanFunctionType;
import static org.oakgp.Type.integerType;
import static org.oakgp.examples.SystemTestConfig.RANDOM;
import static org.oakgp.fitness.TestDataFitnessFunction.createIntegerTestDataFitnessFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.Test;
import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.FunctionSet;
import org.oakgp.RankedCandidate;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.examples.SystemTestConfig;
import org.oakgp.fitness.FitnessFunction;
import org.oakgp.function.Function;
import org.oakgp.function.choice.If;
import org.oakgp.function.classify.IsNegative;
import org.oakgp.function.classify.IsPositive;
import org.oakgp.function.classify.IsZero;
import org.oakgp.function.coll.Count;
import org.oakgp.function.compare.Equal;
import org.oakgp.function.compare.GreaterThan;
import org.oakgp.function.compare.GreaterThanOrEqual;
import org.oakgp.function.compare.LessThan;
import org.oakgp.function.compare.LessThanOrEqual;
import org.oakgp.function.compare.NotEqual;
import org.oakgp.function.hof.Filter;
import org.oakgp.function.math.Add;
import org.oakgp.function.math.Multiply;
import org.oakgp.function.math.Subtract;
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
   private static final Function[] ARITHMETIC_FUNCTIONS = { new Add(), new Subtract(), new Multiply() };

   @Test
   public void testTwoVariableArithmeticExpression() {
      Type[] variableTypes = createTypeArray(2);
      SystemTestConfig config = new SystemTestConfig();
      config.useIntegerConstants(11);
      config.setVariables(variableTypes);
      config.setTerminator(createTerminator());
      config.setFunctionSet(ARITHMETIC_FUNCTIONS);
      FitnessFunction fitnessFunction = createIntegerTestDataFitnessFunction(createTests(variableTypes.length, a -> {
         int x = (int) a.get(0);
         int y = (int) a.get(1);
         return (x * x) + 2 * y + 3 * x + 5;
      }));
      config.setFitnessFunction(fitnessFunction);
      config.process();
   }

   @Test
   public void testThreeVariableArithmeticExpression() {
      Type[] variableTypes = createTypeArray(3);
      SystemTestConfig config = new SystemTestConfig();
      config.useIntegerConstants(11);
      config.setVariables(variableTypes);
      config.setTerminator(createTerminator());
      config.setFunctionSet(ARITHMETIC_FUNCTIONS);
      FitnessFunction fitnessFunction = createIntegerTestDataFitnessFunction(createTests(variableTypes.length, a -> {
         int x = (int) a.get(0);
         int y = (int) a.get(1);
         int z = (int) a.get(2);
         return (x * -3) + (y * 5) - z;
      }));
      config.setFitnessFunction(fitnessFunction);
      config.process();
   }

   @Test
   public void testTwoVariableBooleanLogicExpression() {
      Type[] variableTypes = createTypeArray(2);
      SystemTestConfig config = new SystemTestConfig();
      config.useIntegerConstants(5);
      config.setVariables(variableTypes);
      config.setTerminator(createTerminator());
      config.setFunctionSet(new Add(), new Subtract(), new Multiply(), new LessThan(), new LessThanOrEqual(), new GreaterThan(), new GreaterThanOrEqual(),
            new Equal(), new NotEqual(), new If());
      FitnessFunction fitnessFunction = createIntegerTestDataFitnessFunction(createTests(variableTypes.length, a -> {
         int x = (int) a.get(0);
         int y = (int) a.get(1);
         return x > 20 ? x : y;
      }));
      config.setFitnessFunction(fitnessFunction);
      config.process();
   }

   @Test
   public void testIsCountOfZerosGreater() {
      IsPositive isPositive = new IsPositive();
      IsNegative isNegative = new IsNegative();
      IsZero isZero = new IsZero();

      SystemTestConfig config = new SystemTestConfig();
      config.setConstants(new ConstantNode[] { new ConstantNode(Boolean.TRUE, booleanType()), new ConstantNode(Boolean.FALSE, booleanType()),
            new ConstantNode(isPositive, integerToBooleanFunctionType()), new ConstantNode(isNegative, integerToBooleanFunctionType()),
            new ConstantNode(isZero, integerToBooleanFunctionType()), new ConstantNode(Arguments.createArguments(), integerArrayType()),
            new ConstantNode(0, integerType()) });
      config.setVariables(integerArrayType());
      config.setTerminator(createTerminator());

      FunctionSet functions = new FunctionSet(new Filter(integerType()), isPositive, isNegative, isZero, new Count(integerType()),
            createIdentity(integerArrayType()), createIdentity(integerType()), createIdentity(integerToBooleanFunctionType()));
      config.setFunctionSet(functions);

      Map<Assignments, Integer> testData = new HashMap<>();
      testData.put(createAssignments(createArguments("0", "0", "0", "0", "0", "0", "0", "0")), 8);
      testData.put(createAssignments(createArguments("6", "3", "4", "0", "2", "4", "1", "3")), 1);
      testData.put(createAssignments(createArguments("0", "0", "4", "0", "0", "0", "1", "0")), 6);
      testData.put(createAssignments(createArguments("1", "-1", "2", "5", "4", "-2")), 0);
      testData.put(createAssignments(createArguments("1", "0", "2", "5", "4", "-2")), 1);
      testData.put(createAssignments(createArguments("1", "0", "2", "5", "4", "0")), 2);
      testData.put(createAssignments(createArguments("-2", "0", "8", "7", "0", "-3", "0")), 3);
      testData.put(createAssignments(createArguments("0", "0", "0")), 3);
      FitnessFunction fitnessFunction = createIntegerTestDataFitnessFunction(testData);

      config.setFitnessFunction(fitnessFunction);
      config.process();
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
