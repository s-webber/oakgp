package org.oakgp.examples;

import static java.util.Objects.requireNonNull;
import static org.oakgp.NodeSimplifier.simplify;
import static org.oakgp.TestUtils.writeNode;

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
import org.oakgp.mutate.PointMutation;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.selector.NodeSelectorFactory;
import org.oakgp.selector.WeightedNodeSelectorFactory;
import org.oakgp.tournament.RoundRobinTournament;
import org.oakgp.tournament.TwoPlayerGame;
import org.oakgp.tournament.TwoPlayerGameCache;
import org.oakgp.util.JavaUtilRandomAdapter;
import org.oakgp.util.NodeSet;
import org.oakgp.util.Random;

public class RunBuilder {
   private static final Random RANDOM = new JavaUtilRandomAdapter();
   private static final int ELITISM_SIZE = 3; // TODO
   private static final double RATIO_VARIABLES = .6; // TODO
   private static final int DEFAULT_CACHE_SIZE = 10000;

   private Type _returnType;
   private Random _random;
   private PrimitiveSet _primitiveSet;
   private GenerationProcessor _generationProcessor;
   private GenerationEvolver _generationEvolver;
   private Predicate<List<RankedCandidate>> _terminator;
   private Collection<Node> _initialGeneration;

   public RandomSetter setReturnType(Type returnType) {
      _returnType = requireNonNull(returnType);
      return new RandomSetter();
   }

   public class RandomSetter {
      private RandomSetter() {
      }

      public PrimitiveSetSetter setRandom(Random random) {
         _random = requireNonNull(random);
         return new PrimitiveSetSetter();
      }

      public PrimitiveSetSetter useDefaultRandom() {
         return setRandom(RANDOM);
      }
   }

   public class PrimitiveSetSetter {
      private PrimitiveSetSetter() {
      }

      public GenerationProcessorSetter setPrimitiveSet(PrimitiveSet primitiveSet) {
         _primitiveSet = requireNonNull(primitiveSet);
         return new GenerationProcessorSetter();
      }

      public ConstantsSetter setFunctionSet(FunctionSet functionSet) {
         return new ConstantsSetter(requireNonNull(functionSet));
      }

      public ConstantsSetter setFunctionSet(Function... functions) {
         return setFunctionSet(new FunctionSet(functions));
      }

      public class ConstantsSetter {
         private final FunctionSet functionSet;

         private ConstantsSetter(FunctionSet functionSet) {
            this.functionSet = functionSet;
         }

         public VariablesSetter setConstants(ConstantNode... constants) {
            ConstantSet constantSet = new ConstantSet(constants);
            return new VariablesSetter(functionSet, constantSet);
         }
      }

      public class VariablesSetter {
         private final FunctionSet functionSet;
         private final ConstantSet constantSet;

         private VariablesSetter(FunctionSet functionSet, ConstantSet constantSet) {
            this.functionSet = functionSet;
            this.constantSet = constantSet;
         }

         public GenerationProcessorSetter setVariables(Type... variableTypes) {
            VariableSet variableSet = VariableSet.createVariableSet(variableTypes);
            PrimitiveSet primitiveSet = new PrimitiveSetImpl(functionSet, constantSet, variableSet, _random, RATIO_VARIABLES);
            return setPrimitiveSet(primitiveSet);
         }
      }
   }

   public class GenerationProcessorSetter {
      private GenerationProcessorSetter() {
      }

      public GenerationEvolverSetter setGenerationProcessor(GenerationProcessor generationProcessor) {
         _generationProcessor = requireNonNull(generationProcessor);
         return new GenerationEvolverSetter();
      }

      public GenerationEvolverSetter setFitnessFunction(FitnessFunction fitnessFunction) {
         return setGenerationProcessor(new FitnessFunctionGenerationProcessor(ensureCached(fitnessFunction)));
      }

      private FitnessFunction ensureCached(FitnessFunction fitnessFunction) {
         if (fitnessFunction instanceof FitnessFunctionCache) {
            return fitnessFunction;
         } else {
            return new FitnessFunctionCache(DEFAULT_CACHE_SIZE, fitnessFunction);
         }
      }

      public GenerationEvolverSetter setTwoPlayerGame(TwoPlayerGame twoPlayerGame) {
         return setGenerationProcessor(new RoundRobinTournament(ensureCached(twoPlayerGame)));
      }

      private TwoPlayerGame ensureCached(TwoPlayerGame twoPlayerGame) {
         if (twoPlayerGame instanceof TwoPlayerGameCache) {
            return twoPlayerGame;
         } else {
            return new TwoPlayerGameCache(DEFAULT_CACHE_SIZE, twoPlayerGame);
         }
      }
   }

   public class GenerationEvolverSetter {
      private GenerationEvolverSetter() {
      }

      public TerminatorSetter setGenerationEvolver(GenerationEvolver generationEvolver) {
         _generationEvolver = requireNonNull(generationEvolver);
         return new TerminatorSetter();
      }

      public TerminatorSetter useDefaultGenerationEvolver() {
         NodeSelectorFactory nodeSelectorFactory = new WeightedNodeSelectorFactory(_random);
         GenerationEvolver generationEvolver = new GenerationEvolver(ELITISM_SIZE, nodeSelectorFactory, createNodeEvolvers());
         return setGenerationEvolver(generationEvolver);
      }

      private Map<NodeEvolver, Long> createNodeEvolvers() {
         Map<NodeEvolver, Long> nodeEvolvers = new HashMap<>();
         TreeGenerator treeGenerator = TreeGeneratorImpl.grow(_primitiveSet, _random);
         nodeEvolvers.put(t -> treeGenerator.generate(_returnType, 4), 5L);
         nodeEvolvers.put(new SubtreeCrossover(_random), 21L);
         nodeEvolvers.put(new PointMutation(_random, _primitiveSet), 21L);
         return nodeEvolvers;
      }
   }

   public class TerminatorSetter {
      private TerminatorSetter() {
      }

      public InitialGenerationSetter setTerminator(Predicate<List<RankedCandidate>> terminator) {
         _terminator = requireNonNull(terminator);
         return new InitialGenerationSetter();
      }

      public InitialGenerationSetter useDefaultTerminator() {
         return setTerminator(null);
      }
   }

   public class InitialGenerationSetter {
      private InitialGenerationSetter() {
      }

      public ProccessRunner setInitialGeneration(Collection<Node> initialGeneration) {
         _initialGeneration = requireNonNull(initialGeneration);
         return new ProccessRunner();
      }

      public TreeDepthSetter setInitialGenerationSize(int generationSize) {
         return new TreeDepthSetter(generationSize);
      }

      public class TreeDepthSetter {
         private final int generationSize;

         private TreeDepthSetter(int generationSize) {
            this.generationSize = requiresPositive(generationSize);
         }

         public ProccessRunner setTreeDepth(int treeDepth) {
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

   public class ProccessRunner {
      private ProccessRunner() {
      }

      public Node process() {
         RankedCandidate best = Runner.process(_generationProcessor, _generationEvolver, _terminator, _initialGeneration);
         System.out.println("Best: " + best);
         Node simplifiedBestNode = simplify(best.getNode());
         System.out.println(writeNode(simplifiedBestNode));
         return simplifiedBestNode;
      }
   }

   private static int requiresPositive(int i) { // TODO move to Utils
      if (i > 0) {
         return i;
      } else {
         throw new IllegalArgumentException("Expected a positive integer but got: " + i);
      }
   }
}
