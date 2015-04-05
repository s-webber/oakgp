package org.oakgp.examples;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.oakgp.NodeSimplifier.simplify;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.TestUtils.writeNode;
import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerType;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.oakgp.ConstantSet;
import org.oakgp.FunctionSet;
import org.oakgp.GenerationEvolver;
import org.oakgp.GenerationProcessor;
import org.oakgp.NodeEvolver;
import org.oakgp.PrimitiveSet;
import org.oakgp.RankedCandidate;
import org.oakgp.Runner;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.VariableSet;
import org.oakgp.crossover.SubtreeCrossover;
import org.oakgp.fitness.FitnessFunction;
import org.oakgp.fitness.FitnessFunctionCache;
import org.oakgp.fitness.FitnessFunctionGenerationProcessor;
import org.oakgp.function.Function;
import org.oakgp.function.choice.If;
import org.oakgp.function.compare.Equal;
import org.oakgp.function.compare.GreaterThan;
import org.oakgp.function.compare.GreaterThanOrEqual;
import org.oakgp.function.compare.LessThan;
import org.oakgp.function.compare.LessThanOrEqual;
import org.oakgp.function.compare.NotEqual;
import org.oakgp.function.math.Add;
import org.oakgp.function.math.Multiply;
import org.oakgp.function.math.Subtract;
import org.oakgp.mutate.PointMutation;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.selector.NodeSelectorFactory;
import org.oakgp.selector.WeightedNodeSelectorFactory;
import org.oakgp.util.JavaUtilRandomAdapter;
import org.oakgp.util.NodeSet;
import org.oakgp.util.Random;

public class SystemTestConfig {
   public static final Random RANDOM = new JavaUtilRandomAdapter();
   public static final int GENERATION_SIZE = 50;

   private static final NodeSelectorFactory SELECTOR_FACTORY = new WeightedNodeSelectorFactory(RANDOM);
   private static final int ELITISM_SIZE = 3;
   private static final double RATIO_VARIABLES = .6;

   public static final FunctionSet ARITHMETIC_FUNCTION_SET = createArithmeticFunctionSet();
   public static final FunctionSet COMPARISON_FUNCTION_SET = createComparisonFunctionSet();

   private static FunctionSet createArithmeticFunctionSet() {
      FunctionSet.Builder builder = new FunctionSet.Builder();

      builder.put("+", new Add());
      builder.put("-", new Subtract());
      builder.put("*", new Multiply());

      return builder.build();
   }

   private static FunctionSet createComparisonFunctionSet() {
      FunctionSet.Builder builder = new FunctionSet.Builder();

      builder.put("+", new Add());
      builder.put("-", new Subtract());
      builder.put("*", new Multiply());

      builder.put("<", new LessThan());
      builder.put("<=", new LessThanOrEqual());
      builder.put(">", new GreaterThan());
      builder.put(">=", new GreaterThanOrEqual());
      builder.put("=", new Equal());
      builder.put("!=", new NotEqual());

      builder.put("if", new If());

      return builder.build();
   }

   private FunctionSet functionSet;
   private ConstantSet constantSet;
   private VariableSet variableSet;
   private PrimitiveSet primitiveSet;
   private GenerationProcessor generationProcessor;
   private Predicate<List<RankedCandidate>> terminator;
   private final Type returnType = Type.integerType();

   public void process() {
      requireNonNull(generationProcessor);
      requireNonNull(terminator);

      Collection<Node> initialGeneration = createInitialGeneration(getPrimitiveSet(), GENERATION_SIZE, returnType);
      RankedCandidate best = Runner.process(generationProcessor, getNodeEvolvers(), terminator, initialGeneration);
      System.out.println("Best: " + best);
      System.out.println(writeNode(simplify(best.getNode())));
   }

   private GenerationEvolver getNodeEvolvers() {
      return new GenerationEvolver(ELITISM_SIZE, SELECTOR_FACTORY, createNodeEvolvers(getPrimitiveSet()));
   }

   private PrimitiveSet getPrimitiveSet() {
      if (isNull(primitiveSet)) {
         requireNonNull(functionSet);
         requireNonNull(constantSet);
         requireNonNull(variableSet);

         primitiveSet = new PrimitiveSet(functionSet, constantSet, variableSet, RANDOM, RATIO_VARIABLES);
      }
      return primitiveSet;
   }

   private static Map<NodeEvolver, Long> createNodeEvolvers(PrimitiveSet primitiveSet) {
      Map<NodeEvolver, Long> nodeEvolvers = new HashMap<>();
      nodeEvolvers.put(t -> makeRandomTree(primitiveSet, 4), 5L);
      nodeEvolvers.put(new SubtreeCrossover(RANDOM), 21L);
      nodeEvolvers.put(new PointMutation(RANDOM, primitiveSet), 21L);
      return nodeEvolvers;
   }

   private static Collection<Node> createInitialGeneration(PrimitiveSet primitiveSet, int size, Type type) {
      Set<Node> initialGeneration = new NodeSet();
      for (int i = 0; i < size; i++) {
         Node n = makeRandomTree(primitiveSet, 4, type);
         if (n.getType() != type) {
            throw new RuntimeException();
         }
         initialGeneration.add(n);
      }
      return initialGeneration;
   }

   private static Node makeRandomTree(PrimitiveSet primitiveSet, int depth) {
      return makeRandomTree(primitiveSet, depth, integerType());
   }

   private static Node makeRandomTree(PrimitiveSet primitiveSet, int depth, Type type) {
      if (depth > 0 && RANDOM.nextDouble() < .5) {
         Function function = primitiveSet.nextFunction(type);
         Signature signature = function.getSignature();
         Node[] args = new Node[signature.getArgumentTypesLength()];
         for (int i = 0; i < args.length; i++) {
            Type argType = signature.getArgumentType(i);
            Node arg = makeRandomTree(primitiveSet, depth - 1, argType);
            args[i] = arg;
         }
         return new FunctionNode(function, args);
      } else {
         return primitiveSet.nextTerminal(type);
      }
   }

   public void setConstants(ConstantNode... constants) {
      requireNull(this.constantSet);
      this.constantSet = new ConstantSet(constants);
   }

   public void setVariables(Type... variableTypes) {
      requireNull(this.variableSet);
      this.variableSet = VariableSet.createVariableSet(variableTypes);
   }

   public void setFunctionSet(FunctionSet functionSet) {
      requireNull(this.functionSet);
      this.functionSet = functionSet;
   }

   public void setGenerationProcessor(GenerationProcessor generationProcessor) {
      requireNull(this.generationProcessor);
      this.generationProcessor = generationProcessor;
   }

   public void setTerminator(Predicate<List<RankedCandidate>> terminator) {
      requireNull(this.terminator);
      this.terminator = terminator;
   }

   public void setFitnessFunction(FitnessFunction fitnessFunction) {
      FitnessFunction fitnessFunctionCache = new FitnessFunctionCache(GENERATION_SIZE, fitnessFunction);
      setGenerationProcessor(new FitnessFunctionGenerationProcessor(fitnessFunctionCache));
   }

   public void useArithmeticFunctions() {
      setFunctionSet(ARITHMETIC_FUNCTION_SET);
   }

   public void useComparisionFunctions() {
      setFunctionSet(COMPARISON_FUNCTION_SET);
   }

   public void useIntegerConstants(int numberOfConstants) {
      ConstantNode[] constants = new ConstantNode[numberOfConstants + 2];
      for (int i = 0; i < numberOfConstants; i++) {
         constants[i] = integerConstant(i);
      }
      constants[numberOfConstants] = new ConstantNode(Boolean.TRUE, booleanType());
      constants[numberOfConstants + 1] = new ConstantNode(Boolean.FALSE, booleanType());
      setConstants(constants);
   }

   private void requireNull(Object o) {
      if (nonNull(o)) {
         throw new RuntimeException();
      }
   }
}
