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
import java.util.function.Supplier;
import java.util.logging.Logger;

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

public class SystemTestConfig {
   // TODO enable all values to be overridden
   // TODO provide default terminator implementation

   public static final Random RANDOM = new JavaUtilRandomAdapter();

   private static final int DEFAULT_GENERATION_SIZE = 50;
   private static final int DEFAULT_CACHE_SIZE = 10000;
   private static final java.util.function.Function<Config, Collection<Node>> DEFAULT_INITIAL_GENERATION_CREATOR = (config) -> {
      Set<Node> initialGeneration = new NodeSet();
      TreeGenerator treeGenerator = TreeGeneratorImpl.grow(config.getPrimitiveSet(), config.getRandom());
      for (int i = 0; i < config.getGenerationSize(); i++) {
         Node n = treeGenerator.generate(config.getReturnType(), 4);
         initialGeneration.add(n);
      }
      return initialGeneration;
   };
   private static final int ELITISM_SIZE = 3; // TODO
   private static final double RATIO_VARIABLES = .6; // TODO

   private int generationSize = 0;
   private final Property<Random> random = new Property<>("random");
   private final Property<NodeSelectorFactory> nodeSelectorFactory = new Property<>("nodeSelectorFactory");
   private final Property<FunctionSet> functionSet = new Property<>("functionSet");
   private final Property<ConstantSet> constantSet = new Property<>("constantSet");
   private final Property<VariableSet> variableSet = new Property<>("variableSet");
   private final Property<PrimitiveSet> primitiveSet = new Property<>("primitiveSet");
   private final Property<GenerationProcessor> generationProcessor = new Property<>("generationProcessor");
   private final Property<Predicate<List<RankedCandidate>>> terminator = new Property<>("terminator");
   private final Property<Type> returnType = new Property<>("returnType");
   private final Property<java.util.function.Function<Config, Collection<Node>>> initialGenerationCreator = new Property<>("initialGenerationCreator");
   private final Property<java.util.function.Function<Config, GenerationEvolver>> generationEvolverCreator = new Property<>("generationEvolverCreator");

   public Node process() {
      setDefaultGenerationSize();
      random.setIfAbsent(RANDOM);
      nodeSelectorFactory.setIfAbsent(() -> new WeightedNodeSelectorFactory(random.get()));
      primitiveSet.set(new PrimitiveSetImpl(functionSet.get(), constantSet.get(), variableSet.get(), random.get(), RATIO_VARIABLES));
      initialGenerationCreator.setIfAbsent(DEFAULT_INITIAL_GENERATION_CREATOR);

      Collection<Node> initialGeneration = initialGenerationCreator.get().apply(new Config());
      RankedCandidate best = Runner.process(generationProcessor.get(), getNodeEvolvers(), terminator.get(), initialGeneration);
      System.out.println("Best: " + best);
      Node simplifiedBestNode = simplify(best.getNode());
      System.out.println(writeNode(simplifiedBestNode));
      return simplifiedBestNode;
   }

   private GenerationEvolver getNodeEvolvers() { // TODO
      return new GenerationEvolver(ELITISM_SIZE, nodeSelectorFactory.get(), createNodeEvolvers(returnType.get(), primitiveSet.get()));
   }

   private Map<NodeEvolver, Long> createNodeEvolvers(Type returnType, PrimitiveSet primitiveSet) { // TODO
      Map<NodeEvolver, Long> nodeEvolvers = new HashMap<>();
      TreeGenerator treeGenerator = TreeGeneratorImpl.grow(primitiveSet, random.get());
      nodeEvolvers.put(t -> treeGenerator.generate(returnType, 4), 5L);
      nodeEvolvers.put(new SubtreeCrossover(random.get()), 21L);
      nodeEvolvers.put(new PointMutation(random.get(), primitiveSet), 21L);
      return nodeEvolvers;
   }

   public void setReturnType(Type returnType) {
      this.returnType.set(returnType);
   }

   public void setConstants(ConstantNode... constants) {
      this.constantSet.set(new ConstantSet(constants));
   }

   public void setVariables(Type... variableTypes) {
      this.variableSet.set(VariableSet.createVariableSet(variableTypes));
   }

   private void setFunctionSet(FunctionSet functionSet) {
      this.functionSet.set(functionSet);
   }

   public void setFunctionSet(Function... functions) {
      setFunctionSet(new FunctionSet(functions));
   }

   public void setGenerationProcessor(GenerationProcessor generationProcessor) {
      this.generationProcessor.set(generationProcessor);
   }

   public void setTerminator(Predicate<List<RankedCandidate>> terminator) {
      this.terminator.set(terminator);
   }

   public void setFitnessFunction(FitnessFunction fitnessFunction) {
      setGenerationProcessor(new FitnessFunctionGenerationProcessor(ensureCached(fitnessFunction)));
   }

   private FitnessFunction ensureCached(FitnessFunction fitnessFunction) {
      if (fitnessFunction instanceof FitnessFunctionCache) {
         return fitnessFunction;
      } else {
         return new FitnessFunctionCache(DEFAULT_CACHE_SIZE, fitnessFunction);
      }
   }

   public void setTwoPlayerGame(TwoPlayerGame twoPlayerGame) {
      setGenerationProcessor(new RoundRobinTournament(ensureCached(twoPlayerGame)));
   }

   private TwoPlayerGame ensureCached(TwoPlayerGame twoPlayerGame) {
      if (twoPlayerGame instanceof TwoPlayerGameCache) {
         return twoPlayerGame;
      } else {
         return new TwoPlayerGameCache(DEFAULT_CACHE_SIZE, twoPlayerGame);
      }
   }

   public void setRandom(Random random) {
      this.random.set(random);
   }

   public void setNodeSelectorFactory(NodeSelectorFactory nodeSelectorFactory) {
      this.nodeSelectorFactory.set(nodeSelectorFactory);
   }

   public void setInitialGenerationCreator(java.util.function.Function<Config, Collection<Node>> initialGenerationCreator) {
      this.initialGenerationCreator.set(initialGenerationCreator);
   }

   public void setInitialGeneration(Collection<Node> initialGeneration) {
      this.initialGenerationCreator.set((c) -> initialGeneration);
   }

   public void setGenerationSize(int generationSize) {
      if (this.generationSize > 0) {
         throw new IllegalStateException("Property [generationSize] has already been assigned to [" + this.generationSize + "] so cannot be reassigned to ["
               + generationSize + "]");
      } else if (generationSize < 1) {
         throw new IllegalArgumentException("Property [generationSize] can only be set to a positive value, so cannot be set to [" + generationSize + "]");
      } else {
         this.generationSize = generationSize;
      }
   }

   private void setDefaultGenerationSize() {
      if (generationSize == 0) {
         setGenerationSize(DEFAULT_GENERATION_SIZE);
      }
   }

   private static class Property<T> {
      private final String name;
      private T value;

      Property(String name) {
         this.name = name;
      }

      T get() {
         return requireNonNull(value, "Property  [" + name + "] has not been set");
      }

      void set(T value) {
         if (this.value != null) {
            throw new IllegalStateException("Property [" + name + "] has already been assigned to [" + this.value + "] so cannot be reassigned to [" + value
                  + "]");
         }
         this.value = requireNonNull(value, "Cannot set property [" + name + "] to null");
      }

      void setIfAbsent(Supplier<T> supplier) {
         if (value == null) {
            set(supplier.get());
         }
      }

      void setIfAbsent(T defaultValue) {
         if (value == null) {
            Logger.getGlobal().info("Setting property  [" + name + "] to default value of [" + defaultValue + "]");
            set(defaultValue);
         }
      }
   }

   public class Config {
      public int getGenerationSize() {
         return generationSize;
      }

      public Random getRandom() {
         return random.get();
      }

      public PrimitiveSet getPrimitiveSet() {
         return primitiveSet.get();
      }

      public Type getReturnType() {
         return returnType.get();
      }
   }
}
