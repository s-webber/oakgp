package org.oakgp.util;

import static java.util.Objects.requireNonNull;
import static org.oakgp.NodeSimplifier.simplify;

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
import org.oakgp.GeneticOperator;
import org.oakgp.PrimitiveSet;
import org.oakgp.PrimitiveSetImpl;
import org.oakgp.RankedCandidate;
import org.oakgp.Runner;
import org.oakgp.TreeGenerator;
import org.oakgp.TreeGeneratorImpl;
import org.oakgp.Type;
import org.oakgp.VariableSet;
import org.oakgp.crossover.SubtreeCrossover;
import org.oakgp.fitness.FitnessFunction;
import org.oakgp.fitness.FitnessFunctionCache;
import org.oakgp.fitness.FitnessFunctionGenerationProcessor;
import org.oakgp.function.Function;
import org.oakgp.mutate.ConstantToFunctionMutation;
import org.oakgp.mutate.PointMutation;
import org.oakgp.mutate.SubTreeMutation;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.selector.NodeSelectorFactory;
import org.oakgp.selector.WeightedNodeSelectorFactory;
import org.oakgp.terminate.MaxGenerationTerminator;
import org.oakgp.tournament.RoundRobinTournament;
import org.oakgp.tournament.TwoPlayerGame;
import org.oakgp.tournament.TwoPlayerGameCache;

public final class RunBuilder {
   private static final Random RANDOM = new JavaUtilRandomAdapter();
   private static final int ELITISM_SIZE = 3; // TODO
   private static final double RATIO_VARIABLES = .6;
   private static final int DEFAULT_CACHE_SIZE = 10000;

   private Type _returnType;
   private Random _random = RANDOM;
   private PrimitiveSet _primitiveSet;
   private GenerationProcessor _generationProcessor;
   private GenerationEvolver _generationEvolver;
   private Predicate<List<RankedCandidate>> _terminator;
   private Collection<Node> _initialGeneration;

   public RandomSetter setReturnType(final Type returnType) {
      _returnType = requireNonNull(returnType);
      return new RandomSetter();
   }

   public class RandomSetter extends PrimitiveSetSetter {
      private RandomSetter() {
      }

      public PrimitiveSetSetter setRandom(final Random random) {
         _random = requireNonNull(random);
         return new PrimitiveSetSetter();
      }
   }

   public class PrimitiveSetSetter {
      private PrimitiveSetSetter() {
      }

      public GenerationProcessorSetter setPrimitiveSet(final PrimitiveSet primitiveSet) {
         _primitiveSet = requireNonNull(primitiveSet);
         return new GenerationProcessorSetter();
      }

      public VariablesSetter setConstants(final ConstantNode... constants) {
         ConstantSet constantSet = new ConstantSet(constants);
         return new VariablesSetter(constantSet);
      }

      public VariablesSetter setConstants(final List<ConstantNode> constants) {
         return setConstants(constants.toArray(new ConstantNode[constants.size()]));
      }
   }

   public class VariablesSetter {
      private final ConstantSet constantSet;

      private VariablesSetter(final ConstantSet constantSet) {
         this.constantSet = constantSet;
      }

      public VariablesRatioSetter setVariables(final Type... variableTypes) {
         VariableSet variableSet = VariableSet.createVariableSet(variableTypes);
         return new VariablesRatioSetter(constantSet, variableSet);
      }
   }

   public class VariablesRatioSetter implements FunctionSetSetter {
      private final ConstantSet constantSet;
      private final VariableSet variableSet;

      public VariablesRatioSetter(ConstantSet constantSet, VariableSet variableSet) {
         this.constantSet = constantSet;
         this.variableSet = variableSet;
      }

      public FunctionSetSetter setRatioVariables(final double ratioVariables) {
         // TODO validate 0 <= n <= 1
         return new FunctionSetSetterImpl(constantSet, variableSet, ratioVariables);
      }

      @Override
      public GenerationProcessorSetter setFunctionSet(Function... functions) {
         return setRatioVariables(RATIO_VARIABLES).setFunctionSet(functions);
      }
   }

   public class FunctionSetSetterImpl implements FunctionSetSetter {
      private final ConstantSet constantSet;
      private final VariableSet variableSet;
      private final double ratioVariables;

      private FunctionSetSetterImpl(ConstantSet constantSet, VariableSet variableSet, double ratioVariables) {
         this.constantSet = constantSet;
         this.variableSet = variableSet;
         this.ratioVariables = ratioVariables;
      }

      @Override
      public GenerationProcessorSetter setFunctionSet(final Function... functions) {
         logFunctionSet(functions);

         FunctionSet functionSet = new FunctionSet(functions);
         _primitiveSet = new PrimitiveSetImpl(functionSet, constantSet, variableSet, _random, ratioVariables);
         return new GenerationProcessorSetter();
      }

      private void logFunctionSet(final Function... functions) {
         boolean first = true;
         for (Function function : functions) {
            if (first) {
               first = false;
            } else {
               System.out.println("<br>");
            }
            System.out.println("|Class:|" + function.getClass().getName());
            System.out.println("|Symbol:|" + function.getDisplayName());
            System.out.println("|Return Type:|" + function.getSignature().getReturnType());
            String argumentTypes = function.getSignature().getArgumentTypes().toString();
            argumentTypes = argumentTypes.substring(1, argumentTypes.length() - 1);
            System.out.println("|Arguments:|" + argumentTypes);
         }
      }
   }

   public interface FunctionSetSetter {
      GenerationProcessorSetter setFunctionSet(Function... functions);
   }

   public class GenerationProcessorSetter {
      private GenerationProcessorSetter() {
      }

      public GenerationEvolverSetter setGenerationProcessor(final GenerationProcessor generationProcessor) {
         _generationProcessor = requireNonNull(generationProcessor);
         return new GenerationEvolverSetter();
      }

      public GenerationEvolverSetter setFitnessFunction(final FitnessFunction fitnessFunction) {
         return setGenerationProcessor(new FitnessFunctionGenerationProcessor(ensureCached(fitnessFunction)));
      }

      private FitnessFunction ensureCached(final FitnessFunction fitnessFunction) {
         if (fitnessFunction instanceof FitnessFunctionCache) {
            return fitnessFunction;
         } else {
            return new FitnessFunctionCache(DEFAULT_CACHE_SIZE, fitnessFunction);
         }
      }

      public GenerationEvolverSetter setTwoPlayerGame(final TwoPlayerGame twoPlayerGame) {
         return setGenerationProcessor(new RoundRobinTournament(ensureCached(twoPlayerGame)));
      }

      private TwoPlayerGame ensureCached(final TwoPlayerGame twoPlayerGame) {
         if (twoPlayerGame instanceof TwoPlayerGameCache) {
            return twoPlayerGame;
         } else {
            return new TwoPlayerGameCache(DEFAULT_CACHE_SIZE, twoPlayerGame);
         }
      }
   }

   public class GenerationEvolverSetter extends TerminatorSetter {
      private GenerationEvolverSetter() {
         useDefaultGenerationEvolver();
      }

      private void useDefaultGenerationEvolver() {
         NodeSelectorFactory nodeSelectorFactory = new WeightedNodeSelectorFactory(_random);
         GenerationEvolver generationEvolver = new GenerationEvolver(ELITISM_SIZE, nodeSelectorFactory, createDefaultGeneticOperators());
         setGenerationEvolver(generationEvolver);
      }

      private Map<GeneticOperator, Long> createDefaultGeneticOperators() {
         Map<GeneticOperator, Long> operators = new HashMap<>();
         TreeGenerator treeGenerator = TreeGeneratorImpl.grow(_primitiveSet, _random);
         operators.put(t -> treeGenerator.generate(_returnType, 4), 4L);
         operators.put(new SubtreeCrossover(_random), 19L);
         operators.put(new PointMutation(_random, _primitiveSet), 19L);
         operators.put(new SubTreeMutation(_random, treeGenerator), 2L);
         operators.put(new ConstantToFunctionMutation(_random, TreeGeneratorImpl.full(_primitiveSet)), 2L);
         return operators;
      }

      public TerminatorSetter setGenerationEvolver(final java.util.function.Function<Config, GenerationEvolver> generationEvolver) {
         return setGenerationEvolver(generationEvolver.apply(new Config()));
      }

      private TerminatorSetter setGenerationEvolver(GenerationEvolver generationEvolver) {
         _generationEvolver = requireNonNull(generationEvolver);
         return new TerminatorSetter();
      }
   }

   public class TerminatorSetter {
      private TerminatorSetter() {
      }

      public InitialGenerationSetter setTerminator(final Predicate<List<RankedCandidate>> terminator) {
         _terminator = requireNonNull(terminator);
         return new InitialGenerationSetter();
      }

      public InitialGenerationSetter setMaxGenerations(final int maxGenerations) {
         return setTerminator(new MaxGenerationTerminator(maxGenerations));
      }
   }

   public class InitialGenerationSetter {
      private InitialGenerationSetter() {
      }

      public ProcessRunner setInitialGeneration(final java.util.function.Function<Config, Collection<Node>> initialGeneration) {
         return setInitialGeneration(initialGeneration.apply(new Config()));
      }

      private ProcessRunner setInitialGeneration(Collection<Node> initialGeneration) {
         _initialGeneration = requireNonNull(initialGeneration);
         return new ProcessRunner();
      }

      public TreeDepthSetter setInitialGenerationSize(final int generationSize) {
         return new TreeDepthSetter(generationSize);
      }

      public class TreeDepthSetter {
         private final int generationSize;

         private TreeDepthSetter(final int generationSize) {
            this.generationSize = requiresPositive(generationSize);
         }

         public ProcessRunner setTreeDepth(final int treeDepth) {
            requiresPositive(treeDepth);

            Set<Node> initialGeneration = new NodeSet();
            TreeGenerator treeGenerator = TreeGeneratorImpl.grow(_primitiveSet, _random);
            for (int i = 0; i < generationSize; i++) {
               Node n = treeGenerator.generate(_returnType, treeDepth);
               initialGeneration.add(n);
            }
            return setInitialGeneration(initialGeneration);
         }
      }
   }

   public class ProcessRunner {
      private ProcessRunner() {
      }

      public Node process() {
         RankedCandidate best = Runner.process(_generationProcessor, _generationEvolver, _terminator, _initialGeneration);
         System.out.println("Best: " + best);
         Node simplifiedBestNode = simplify(best.getNode());
         System.out.println(simplifiedBestNode);
         return simplifiedBestNode;
      }
   }

   private static int requiresPositive(final int i) { // TODO move to Utils
      if (i > 0) {
         return i;
      } else {
         throw new IllegalArgumentException("Expected a positive integer but got: " + i);
      }
   }

   public class Config {
      public PrimitiveSet getPrimitiveSet() {
         return _primitiveSet;
      }

      public Random getRandom() {
         return _random;
      }

      public Type getReturnType() {
         return _returnType;
      }
   }
}
