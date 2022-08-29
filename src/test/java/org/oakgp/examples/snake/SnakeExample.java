package org.oakgp.examples.snake;

import static org.oakgp.type.CommonTypes.booleanType;

import java.util.ArrayList;
import java.util.List;

import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.function.choice.If;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.rank.RankedCandidates;
import org.oakgp.type.CommonTypes;
import org.oakgp.type.TypeBuilder;
import org.oakgp.type.Types.Type;
import org.oakgp.util.FunctionSetBuilder;
import org.oakgp.util.RunBuilder;
import org.oakgp.util.Utils;

public class SnakeExample {
   private static final Type DIRECTION_TYPE = TypeBuilder.name("direction").parents(CommonTypes.comparableType()).build();
   private static final Type DIRECTION_TYPE2 = TypeBuilder.name("direction2").parents(CommonTypes.comparableType()).build();
   private static final Type DISTANCES_TYPE = TypeBuilder.name("distances").build();
   private static final int INITIAL_POPULATION_SIZE = 200;
   private static final int INITIAL_POPULATION_MAX_DEPTH = 4;
   private static final int MAX_GENERATIONS = 1000;
   private static final int GRID_WIDTH = 6;

   public static void main(String[] args) throws Exception {
      FunctionSet functionSet = new FunctionSetBuilder() //
                  .add(new If(), DIRECTION_TYPE2) //
                  .add(new IsSafe1()) //
                  .add(new IsSafe2()) //
                  .add(new Convert()) //
                  .build();
      List<ConstantNode> constants = new ArrayList<>();
      constants.add(Utils.TRUE_NODE);
      constants.add(Utils.FALSE_NODE);
      constants.addAll(Utils.createEnumConstants(Direction.class, DIRECTION_TYPE));
      constants.addAll(Utils.createEnumConstants(Direction.class, DIRECTION_TYPE2));
      Snake fitnessFunction = new Snake(GRID_WIDTH);
      RankedCandidates output = new RunBuilder(). //
                  setReturnType(DIRECTION_TYPE2). //
                  setConstants(constants). //
                  setVariables(DISTANCES_TYPE, DIRECTION_TYPE). //
                  setFunctionSet(functionSet). //
                  setFitnessFunction(fitnessFunction). //
                  setInitialPopulationSize(INITIAL_POPULATION_SIZE). //
                  setTreeDepth(INITIAL_POPULATION_MAX_DEPTH). //
                  setTargetFitness(0). //
                  setMaxGenerations(MAX_GENERATIONS). //
                  process();
      Node best = output.best().getNode();
      System.out.println(best);
      fitnessFunction.evaluate(best, true);
      System.out.println(best);
   }

   private static class IsSafe1 implements Function {
      @Override
      public Object evaluate(ChildNodes arguments, Assignments assignments) {
         int[] distances = arguments.first().evaluate(assignments);
         Direction direction = arguments.second().evaluate(assignments);
         return distances[direction.ordinal()] > 0;
      }

      @Override
      public Signature getSignature() {
         return Signature.createSignature(booleanType(), DISTANCES_TYPE, DIRECTION_TYPE);
      }
   }

   private static class IsSafe2 implements Function {
      @Override
      public Object evaluate(ChildNodes arguments, Assignments assignments) {
         int[] distances = arguments.first().evaluate(assignments);
         Direction direction = arguments.second().evaluate(assignments);
         return distances[direction.ordinal()] > 1;
      }

      @Override
      public Signature getSignature() {
         return Signature.createSignature(booleanType(), DISTANCES_TYPE, DIRECTION_TYPE);
      }
   }

   private static class Convert implements Function {
      @Override
      public Object evaluate(ChildNodes arguments, Assignments assignments) {
         return arguments.first().evaluate(assignments);
      }

      @Override
      public Signature getSignature() {
         return Signature.createSignature(DIRECTION_TYPE2, DIRECTION_TYPE);
      }
   }
}
