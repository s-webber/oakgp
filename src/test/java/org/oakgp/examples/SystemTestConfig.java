package org.oakgp.examples;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.oakgp.NodeSimplifier.simplify;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.TestUtils.writeNode;
import static org.oakgp.Type.booleanType;

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
import org.oakgp.util.JavaUtilRandomAdapter;
import org.oakgp.util.NodeSet;
import org.oakgp.util.Random;

public class SystemTestConfig {
   // TODO enable all values to be overridden
   // TODO provide default terminator implementation

   public static final Random RANDOM = new JavaUtilRandomAdapter();
   public static final int GENERATION_SIZE = 50;

   private static final NodeSelectorFactory SELECTOR_FACTORY = new WeightedNodeSelectorFactory(RANDOM);
   private static final int ELITISM_SIZE = 3;
   private static final double RATIO_VARIABLES = .6;

   private FunctionSet functionSet;
   private ConstantSet constantSet;
   private VariableSet variableSet;
   private PrimitiveSet primitiveSet;
   private GenerationProcessor generationProcessor;
   private Predicate<List<RankedCandidate>> terminator;
   private Type returnType;

   public void process() {
      requireNonNull(generationProcessor);
      requireNonNull(terminator);
      requireNonNull(returnType);

      TreeGenerator treeGenerator = TreeGeneratorImpl.grow(getPrimitiveSet(), RANDOM);
      Collection<Node> initialGeneration = createInitialGeneration(treeGenerator, returnType, GENERATION_SIZE);
      RankedCandidate best = Runner.process(generationProcessor, getNodeEvolvers(), terminator, initialGeneration);
      System.out.println("Best: " + best);
      System.out.println(writeNode(simplify(best.getNode())));
   }

   private GenerationEvolver getNodeEvolvers() {
      return new GenerationEvolver(ELITISM_SIZE, SELECTOR_FACTORY, createNodeEvolvers(returnType, getPrimitiveSet()));
   }

   private PrimitiveSet getPrimitiveSet() {
      if (isNull(primitiveSet)) {
         requireNonNull(functionSet);
         requireNonNull(constantSet);
         requireNonNull(variableSet);

         primitiveSet = new PrimitiveSetImpl(functionSet, constantSet, variableSet, RANDOM, RATIO_VARIABLES);
      }
      return primitiveSet;
   }

   private static Map<NodeEvolver, Long> createNodeEvolvers(Type returnType, PrimitiveSet primitiveSet) {
      Map<NodeEvolver, Long> nodeEvolvers = new HashMap<>();
      TreeGenerator treeGenerator = TreeGeneratorImpl.grow(primitiveSet, RANDOM);
      nodeEvolvers.put(t -> treeGenerator.generate(returnType, 4), 5L);
      nodeEvolvers.put(new SubtreeCrossover(RANDOM), 21L);
      nodeEvolvers.put(new PointMutation(RANDOM, primitiveSet), 21L);
      return nodeEvolvers;
   }

   private static Collection<Node> createInitialGeneration(TreeGenerator treeGenerator, Type type, int generationSize) {
      Set<Node> initialGeneration = new NodeSet();
      for (int i = 0; i < generationSize; i++) {
         Node n = treeGenerator.generate(type, 4);
         initialGeneration.add(n);
      }
      return initialGeneration;
   }

   public void setReturnType(Type returnType) {
      requireNull(this.returnType);
      this.returnType = returnType;
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

   public void setFunctionSet(Function... functions) {
      setFunctionSet(new FunctionSet(functions));
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
