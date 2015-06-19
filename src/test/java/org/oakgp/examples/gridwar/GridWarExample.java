package org.oakgp.examples.gridwar;

import static org.oakgp.TestUtils.createIntegerTypeArray;
import static org.oakgp.Type.integerType;
import static org.oakgp.examples.SystemTestConfig.RANDOM;

import org.oakgp.Type;
import org.oakgp.examples.RunBuilder;
import org.oakgp.function.Function;
import org.oakgp.function.choice.If;
import org.oakgp.function.compare.Equal;
import org.oakgp.function.compare.GreaterThan;
import org.oakgp.function.compare.GreaterThanOrEqual;
import org.oakgp.function.compare.LessThan;
import org.oakgp.function.compare.LessThanOrEqual;
import org.oakgp.function.compare.NotEqual;
import org.oakgp.function.math.IntegerUtils;
import org.oakgp.node.ConstantNode;
import org.oakgp.tournament.FirstPlayerAdvantageGame;
import org.oakgp.tournament.TwoPlayerGame;
import org.oakgp.util.Utils;

public class GridWarExample {
   private static final int NUM_VARIABLES = 5;
   private static final int NUM_GENERATIONS = 10;
   private static final int INITIAL_GENERATION_SIZE = 50;
   private static final int INITIAL_GENERATION_MAX_DEPTH = 4;

   public static void main(String[] args) {
      Function[] functions = { IntegerUtils.INTEGER_UTILS.getAdd(), IntegerUtils.INTEGER_UTILS.getSubtract(), IntegerUtils.INTEGER_UTILS.getMultiply(),
            new LessThan(integerType()), new LessThanOrEqual(integerType()), new GreaterThan(integerType()), new GreaterThanOrEqual(integerType()),
            new Equal(integerType()), new NotEqual(integerType()), new If(integerType()) };
      ConstantNode[] constants = Utils.createIntegerConstants(0, 4);
      Type[] variables = createIntegerTypeArray(NUM_VARIABLES);
      TwoPlayerGame game = new FirstPlayerAdvantageGame(new GridWar(RANDOM));

      new RunBuilder().setReturnType(integerType()).setConstants(constants).setVariables(variables).setFunctionSet(functions).setTwoPlayerGame(game)
            .setMaxGenerations(NUM_GENERATIONS).setInitialGenerationSize(INITIAL_GENERATION_SIZE).setTreeDepth(INITIAL_GENERATION_MAX_DEPTH).process();
   }
}
