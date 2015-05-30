package org.oakgp.examples.gridwar;

import static org.oakgp.TestUtils.createIntegerConstants;
import static org.oakgp.TestUtils.createTypeArray;
import static org.oakgp.Type.integerType;
import static org.oakgp.examples.SystemTestConfig.RANDOM;

import java.util.List;
import java.util.function.Predicate;

import org.junit.Test;
import org.oakgp.RankedCandidate;
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

public class GridWarSystemTest {
   private static final int NUM_VARIABLES = 5;
   private static final int NUM_GENERATIONS = 100;
   private static final int NUM_CONSTANTS = 5;
   private static final int INITIAL_GENERATION_SIZE = 50;
   private static final int INITIAL_GENERATION_MAX_DEPTH = 4;

   @Test
   public void test() {
      Function[] functions = new Function[] { IntegerUtils.INTEGER_UTILS.getAdd(), IntegerUtils.INTEGER_UTILS.getSubtract(),
            IntegerUtils.INTEGER_UTILS.getMultiply(), new LessThan(integerType()), new LessThanOrEqual(integerType()), new GreaterThan(integerType()),
            new GreaterThanOrEqual(integerType()), new Equal(integerType()), new NotEqual(integerType()), new If(integerType()) };
      ConstantNode[] constants = createIntegerConstants(NUM_CONSTANTS);
      Type[] variables = createTypeArray(NUM_VARIABLES);
      TwoPlayerGame game = new FirstPlayerAdvantageGame(new GridWar(RANDOM));
      Predicate<List<RankedCandidate>> terminator = createTerminator();

      new RunBuilder().setReturnType(integerType()).useDefaultRandom().setFunctionSet(functions).setConstants(constants).setVariables(variables)
            .setTwoPlayerGame(game).useDefaultGenerationEvolver().setTerminator(terminator).setInitialGenerationSize(INITIAL_GENERATION_SIZE)
            .setTreeDepth(INITIAL_GENERATION_MAX_DEPTH).process();
   }

   private Predicate<List<RankedCandidate>> createTerminator() {
      return new Predicate<List<RankedCandidate>>() {
         int ctr = 1;

         @Override
         public boolean test(List<RankedCandidate> t) {
            if (ctr % 50 == 0) {
               System.out.println(ctr);
            }
            return ctr++ > NUM_GENERATIONS;
         }
      };
   }
}
