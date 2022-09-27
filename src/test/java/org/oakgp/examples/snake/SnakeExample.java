package org.oakgp.examples.snake;

import static org.oakgp.util.Void.VOID_TYPE;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import org.oakgp.Assignments;
import org.oakgp.function.ImpureFunction;
import org.oakgp.function.Signature;
import org.oakgp.function.choice.If;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.primitive.VariableSet;
import org.oakgp.rank.RankedCandidates;
import org.oakgp.serialize.NodeReader;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;
import org.oakgp.util.FunctionSetBuilder;
import org.oakgp.util.RunBuilder;

public class SnakeExample {
   private static final Type SNAKE_TYPE = Types.type(Snake.class);
   private static final int INITIAL_POPULATION_SIZE = 10000;
   private static final int MAX_GENERATIONS = 500;
   private static final int INITIAL_POPULATION_MAX_DEPTH = 8;

   public static void main(String[] args) throws Exception {
      FunctionSet functionSet = new FunctionSetBuilder() //
            .add(new If(), VOID_TYPE) //
            .add(new BiSequence()) //
            .addMethods(Snake.class, "forward", "left", "right", "isFoodAhead", "isDangerAhead", "isDangerLeft", "isDangerRight", "isDangerTwoAhead",
                  "isFoodUp", "isFoodRight", "isMovingUp", "isMovingDown", "isMovingLeft", "isMovingRight") //
            .build();
      SnakeFitnessFunction fitnessFunction = new SnakeFitnessFunction();

      if (false) {
         List<String> lines = Files.readAllLines(new File("src/test/java/org/oakgp/examples/snake/example.tmp").toPath());
         String syntax = String.join(" ", lines);
         NodeReader nr = new NodeReader(syntax, functionSet, new ConstantNode[0], VariableSet.createVariableSet(SNAKE_TYPE));
         Node node = nr.readNode();
         System.out.println(node);
         fitnessFunction.evaluate(node, true);
         System.out.println("EXIT");
         System.exit(1);
      }

      RankedCandidates output = new RunBuilder(). //
            setReturnType(VOID_TYPE). //
            setConstants(). //
            setVariables(SNAKE_TYPE). //
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
}

class BiSequence implements ImpureFunction {
   @Override
   public Signature getSignature() {
      return Signature.createSignature(VOID_TYPE, VOID_TYPE, VOID_TYPE);
   }

   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments) {
      arguments.first().evaluate(assignments);
      arguments.second().evaluate(assignments);
      return null;
   }
}
