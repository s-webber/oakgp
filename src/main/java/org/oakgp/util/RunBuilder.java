/*
 * Copyright 2015 S. Webber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oakgp.util;

import static java.util.Objects.requireNonNull;
import static org.oakgp.NodeSimplifier.simplify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Logger;

import org.oakgp.Runner;
import org.oakgp.Type;
import org.oakgp.evolve.GenerationEvolver;
import org.oakgp.evolve.GenerationEvolverImpl;
import org.oakgp.evolve.GeneticOperator;
import org.oakgp.evolve.crossover.SubtreeCrossover;
import org.oakgp.evolve.mutate.ConstantToFunctionMutation;
import org.oakgp.evolve.mutate.PointMutation;
import org.oakgp.evolve.mutate.SubTreeMutation;
import org.oakgp.function.Function;
import org.oakgp.generate.TreeGenerator;
import org.oakgp.generate.TreeGeneratorImpl;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.primitive.ConstantSet;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.primitive.PrimitiveSet;
import org.oakgp.primitive.PrimitiveSetImpl;
import org.oakgp.primitive.VariableSet;
import org.oakgp.rank.GenerationProcessor;
import org.oakgp.rank.RankedCandidate;
import org.oakgp.rank.fitness.FitnessFunction;
import org.oakgp.rank.fitness.FitnessFunctionCache;
import org.oakgp.rank.fitness.FitnessFunctionGenerationProcessor;
import org.oakgp.rank.tournament.RoundRobinTournament;
import org.oakgp.rank.tournament.TwoPlayerGame;
import org.oakgp.rank.tournament.TwoPlayerGameCache;
import org.oakgp.select.NodeSelectorFactory;
import org.oakgp.select.RankSelectionFactory;
import org.oakgp.terminate.CompositeTerminator;
import org.oakgp.terminate.MaxGenerationsTerminator;
import org.oakgp.terminate.MaxGenerationsWithoutImprovementTerminator;
import org.oakgp.terminate.TargetFitnessTerminator;

/** Provides a convenient way to configure and start a genetic programming run. */
public final class RunBuilder {
   private static final Random RANDOM = new JavaUtilRandomAdapter();
   private static final double RATIO_VARIABLES = .6;
   private static final int DEFAULT_CACHE_SIZE = 10000;

   private Type _returnType;
   private Random _random = RANDOM;
   private PrimitiveSet _primitiveSet;
   private GenerationProcessor _generationProcessor;
   private GenerationEvolver _generationEvolver;
   private Collection<Node> _initialPopulation;

   public RandomSetter setReturnType(final Type returnType) {
      _returnType = requireNonNull(returnType);
      return new RandomSetter();
   }

   public final class RandomSetter extends PrimitiveSetSetter {
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

   public final class VariablesSetter {
      private final ConstantSet constantSet;

      private VariablesSetter(final ConstantSet constantSet) {
         this.constantSet = constantSet;
      }

      public VariablesRatioSetter setVariables(final Type... variableTypes) {
         VariableSet variableSet = VariableSet.createVariableSet(variableTypes);
         return new VariablesRatioSetter(constantSet, variableSet);
      }
   }

   public final class VariablesRatioSetter implements FunctionSetSetter {
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

   public final class FunctionSetSetterImpl implements FunctionSetSetter {
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

      private void logFunctionSet(final Function[] functions) {
         boolean first = true;
         Arrays.sort(functions, (o1, o2) -> o1.getClass().getName().compareTo(o2.getClass().getName()));
         for (Function function : functions) {
            if (first) {
               first = false;
            } else {
               System.out.println("<br>");
            }
            System.out.println("|Class:|" + function.getClass().getName());
            System.out.println("|Symbol:|" + function.getDisplayName().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"));
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

   public final class GenerationProcessorSetter {
      private GenerationProcessorSetter() {
      }

      // TODO use and an example and then add to how2 guide
      public InitialPopulationSetter setGenerationProcessor(final GenerationProcessor generationProcessor) {
         _generationProcessor = requireNonNull(generationProcessor);
         return new InitialPopulationSetter();
      }

      public InitialPopulationSetter setFitnessFunction(final FitnessFunction fitnessFunction) {
         return setGenerationProcessor(new FitnessFunctionGenerationProcessor(ensureCached(fitnessFunction)));
      }

      private FitnessFunction ensureCached(final FitnessFunction fitnessFunction) {
         if (fitnessFunction instanceof FitnessFunctionCache) {
            return fitnessFunction;
         } else {
            return new FitnessFunctionCache(DEFAULT_CACHE_SIZE, fitnessFunction);
         }
      }

      public InitialPopulationSetter setTwoPlayerGame(final TwoPlayerGame twoPlayerGame) {
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

   public class InitialPopulationSetter {
      private InitialPopulationSetter() {
      }

      // TODO use and document in how2 guide
      public FirstTerminatorSetter setInitialPopulation(final java.util.function.Function<Config, Collection<Node>> initialGeneration) {
         return setInitialPopulation(initialGeneration.apply(new Config()));
      }

      private GenerationEvolverSetter setInitialPopulation(Collection<Node> initialGeneration) {
         _initialPopulation = requireNonNull(initialGeneration);
         return new GenerationEvolverSetter();
      }

      public TreeDepthSetter setInitialPopulationSize(final int generationSize) {
         return new TreeDepthSetter(generationSize);
      }

      public class TreeDepthSetter {
         private final int generationSize;

         private TreeDepthSetter(final int generationSize) {
            this.generationSize = requiresPositive(generationSize);
         }

         public GenerationEvolverSetter setTreeDepth(final int treeDepth) {
            requiresPositive(treeDepth);

            // TODO use Set<Node> initialPopulation = new NodeSet();?
            // TODO do 50:50 split of grow/full?
            Collection<Node> initialPopulation = new ArrayList<>();
            TreeGenerator treeGenerator = TreeGeneratorImpl.grow(_primitiveSet, _random);
            for (int i = 0; i < generationSize; i++) {
               Node n = treeGenerator.generate(_returnType, treeDepth);
               initialPopulation.add(n);
            }
            return setInitialPopulation(initialPopulation);
         }
      }
   }

   public final class GenerationEvolverSetter extends FirstTerminatorSetter {
      private GenerationEvolverSetter() {
         GenerationEvolver defaultGenerationEvolver = createDefaultGenerationEvolver();
         setGenerationEvolver(defaultGenerationEvolver);
      }

      private GenerationEvolver createDefaultGenerationEvolver() {
         int populationSize = _initialPopulation.size();
         NodeSelectorFactory nodeSelectorFactory = new RankSelectionFactory(_random);
         Map<GeneticOperator, Integer> operators = createDefaultGeneticOperators(populationSize);
         int operatorsSize = operators.values().stream().mapToInt(l -> l).sum();
         int elitismSize = populationSize - operatorsSize;
         Logger.getGlobal().info("total: " + populationSize + " elitism: " + elitismSize + " " + operators);
         return new GenerationEvolverImpl(elitismSize, nodeSelectorFactory, operators);
      }

      private Map<GeneticOperator, Integer> createDefaultGeneticOperators(int populationSize) {
         Map<GeneticOperator, Integer> operators = new HashMap<>();
         TreeGenerator treeGenerator = TreeGeneratorImpl.grow(_primitiveSet, _random);
         operators.put(t -> treeGenerator.generate(_returnType, 4), ratio(populationSize, .08));
         operators.put(new SubtreeCrossover(_random, 5), ratio(populationSize, .4));
         operators.put(new PointMutation(_random, _primitiveSet), ratio(populationSize, .4));
         operators.put(new SubTreeMutation(_random, treeGenerator), ratio(populationSize, .04));
         operators.put(new ConstantToFunctionMutation(_random, TreeGeneratorImpl.full(_primitiveSet)), ratio(populationSize, .04));
         return operators;
      }

      private int ratio(int whole, double ratio) {
         return (int) (whole * ratio);
      }

      public InitialPopulationSetter setGenerationEvolver(final java.util.function.Function<Config, GenerationEvolver> generationEvolver) {
         return setGenerationEvolver(generationEvolver.apply(new Config()));
      }

      private InitialPopulationSetter setGenerationEvolver(GenerationEvolver generationEvolver) {
         _generationEvolver = requireNonNull(generationEvolver);
         return new InitialPopulationSetter();
      }
   }

   public class FirstTerminatorSetter {
      private final List<Predicate<List<RankedCandidate>>> terminators = new ArrayList<>();

      private FirstTerminatorSetter() {
      }

      public SubsequentTerminatorSetter setTerminator(final Predicate<List<RankedCandidate>> terminator) {
         terminators.add(terminator);
         return new SubsequentTerminatorSetter(terminators);
      }

      public MaxGenerationsTerminatorSetter setTargetFitness(double targetFitness) {
         return new TargetFitnessTerminatorSetter(terminators).setTargetFitness(targetFitness);
      }

      public MaxGenerationsWithoutImprovementTerminatorSetter setMaxGenerations(final int maxGenerations) {
         return new MaxGenerationsTerminatorSetter(terminators).setMaxGenerations(maxGenerations);
      }

      public ProcessRunner setMaxGenerationsWithoutImprovement(int maxGenerationsWithoutImprovement) {
         return new MaxGenerationsWithoutImprovementTerminatorSetter(terminators).setMaxGenerationsWithoutImprovement(maxGenerationsWithoutImprovement);
      }
   }

   public final class SubsequentTerminatorSetter extends MaxGenerationsTerminatorSetter {
      private SubsequentTerminatorSetter(List<Predicate<List<RankedCandidate>>> terminators) {
         super(terminators);
      }

      public SubsequentTerminatorSetter setTerminator(final Predicate<List<RankedCandidate>> terminator) {
         terminators.add(terminator);
         return this;
      }
   }

   public final class TargetFitnessTerminatorSetter extends MaxGenerationsTerminatorSetter {
      private TargetFitnessTerminatorSetter(List<Predicate<List<RankedCandidate>>> terminators) {
         super(terminators);
      }

      public MaxGenerationsTerminatorSetter setTargetFitness(double targetFitness) {
         terminators.add(new TargetFitnessTerminator(c -> c.getFitness() == targetFitness));
         return new MaxGenerationsTerminatorSetter(terminators);
      }
   }

   public class MaxGenerationsTerminatorSetter extends MaxGenerationsWithoutImprovementTerminatorSetter {
      private MaxGenerationsTerminatorSetter(List<Predicate<List<RankedCandidate>>> terminators) {
         super(terminators);
      }

      public final MaxGenerationsWithoutImprovementTerminatorSetter setMaxGenerations(int maxGenerations) {
         terminators.add(new MaxGenerationsTerminator(maxGenerations));
         return new MaxGenerationsWithoutImprovementTerminatorSetter(terminators);
      }
   }

   public class MaxGenerationsWithoutImprovementTerminatorSetter {
      protected final List<Predicate<List<RankedCandidate>>> terminators;

      private MaxGenerationsWithoutImprovementTerminatorSetter(List<Predicate<List<RankedCandidate>>> terminators) {
         this.terminators = terminators;
      }

      public final ProcessRunner setMaxGenerationsWithoutImprovement(int maxGenerationsWithoutImprovement) {
         terminators.add(new MaxGenerationsWithoutImprovementTerminator(maxGenerationsWithoutImprovement));
         return new ProcessRunner(terminators);
      }

      public final Node process() {
         return new ProcessRunner(terminators).process();
      }
   }

   public final class ProcessRunner {
      private Predicate<List<RankedCandidate>> terminator;

      @SuppressWarnings("unchecked")
      private ProcessRunner(List<Predicate<List<RankedCandidate>>> terminators) {
         if (terminators.isEmpty()) {
            throw new IllegalStateException("No termination criteria set");
         } else if (terminators.size() == 1) {
            terminator = terminators.get(0);
         } else {
            terminator = new CompositeTerminator(terminators.toArray(new Predicate[terminators.size()]));
         }
      }

      public Node process() {
         assertVariablesSet();
         RankedCandidate best = Runner.process(_generationProcessor, _generationEvolver, terminator, _initialPopulation);
         System.out.println("Best: " + best);
         Node simplifiedBestNode = simplify(best.getNode());
         System.out.println(simplifiedBestNode);
         return simplifiedBestNode;
      }

      private void assertVariablesSet() {
         requireNonNull(_generationProcessor);
         requireNonNull(_generationEvolver);
         requireNonNull(terminator);
         requireNonNull(_initialPopulation);
      }
   }

   private static int requiresPositive(final int i) { // TODO move to Utils
      if (i > 0) {
         return i;
      } else {
         throw new IllegalArgumentException("Expected a positive integer but got: " + i);
      }
   }

   public final class Config {
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
