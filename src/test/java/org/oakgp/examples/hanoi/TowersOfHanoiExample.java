package org.oakgp.examples.hanoi;

import static org.oakgp.Type.integerType;
import static org.oakgp.Type.nullableType;
import static org.oakgp.Type.type;

import java.util.Arrays;

import org.oakgp.Type;
import org.oakgp.examples.RunBuilder;
import org.oakgp.fitness.FitnessFunction;
import org.oakgp.function.Function;
import org.oakgp.function.choice.If;
import org.oakgp.function.choice.SwitchEnum;
import org.oakgp.function.compare.Equal;
import org.oakgp.function.compare.GreaterThan;
import org.oakgp.function.compare.LessThan;
import org.oakgp.function.math.IntegerUtils;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.util.Utils;

public class TowersOfHanoiExample {
   static final Type STATE_TYPE = type("gameState");
   static final Type MOVE_TYPE = type("move");
   static final Type POLE_TYPE = type("pole");

   private static final int NUM_GENERATIONS = 1000;
   private static final int INITIAL_GENERATION_SIZE = 100;
   private static final int INITIAL_GENERATION_MAX_DEPTH = 4;

   public static void main(String[] args) {
      Function[] functions = { new If(MOVE_TYPE), new Equal(MOVE_TYPE), new IsValid(), new SwitchEnum(Move.class, nullableType(MOVE_TYPE), MOVE_TYPE),
            new GreaterThan(integerType()), new LessThan(integerType()), new Equal(integerType()), new Next() };
      ConstantNode[] constants = Utils.createEnumConstants(Move.class, MOVE_TYPE);
      constants = Arrays.copyOf(constants, constants.length + 5);
      constants[constants.length - 5] = IntegerUtils.INTEGER_UTILS.zero();
      constants[constants.length - 4] = Utils.TRUE_NODE;
      constants[constants.length - 3] = new ConstantNode(Pole.LEFT, POLE_TYPE);
      constants[constants.length - 2] = new ConstantNode(Pole.MIDDLE, POLE_TYPE);
      constants[constants.length - 1] = new ConstantNode(Pole.RIGHT, POLE_TYPE);
      Type[] variables = { STATE_TYPE, nullableType(MOVE_TYPE) };
      FitnessFunction fitnessFunction = new TowersOfHanoiFitnessFunction(false);

      Node best = new RunBuilder().setReturnType(MOVE_TYPE).setConstants(constants).setVariables(variables).setFunctionSet(functions)
            .setFitnessFunction(fitnessFunction).setMaxGenerations(NUM_GENERATIONS).setInitialGenerationSize(INITIAL_GENERATION_SIZE)
            .setTreeDepth(INITIAL_GENERATION_MAX_DEPTH).process();

      new TowersOfHanoiFitnessFunction(true).evaluate(best);
   }
}
