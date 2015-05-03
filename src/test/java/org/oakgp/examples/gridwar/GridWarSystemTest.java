package org.oakgp.examples.gridwar;

import static org.oakgp.TestUtils.createTypeArray;
import static org.oakgp.Type.integerType;
import static org.oakgp.examples.SystemTestConfig.GENERATION_SIZE;
import static org.oakgp.examples.SystemTestConfig.RANDOM;

import java.util.List;
import java.util.function.Predicate;

import org.junit.Test;
import org.oakgp.RankedCandidate;
import org.oakgp.Type;
import org.oakgp.examples.SystemTestConfig;
import org.oakgp.function.choice.If;
import org.oakgp.function.compare.Equal;
import org.oakgp.function.compare.GreaterThan;
import org.oakgp.function.compare.GreaterThanOrEqual;
import org.oakgp.function.compare.LessThan;
import org.oakgp.function.compare.LessThanOrEqual;
import org.oakgp.function.compare.NotEqual;
import org.oakgp.function.math.IntegerUtils;
import org.oakgp.tournament.FirstPlayerAdvantageGame;
import org.oakgp.tournament.RoundRobinTournament;
import org.oakgp.tournament.TwoPlayerGame;
import org.oakgp.tournament.TwoPlayerGameCache;

public class GridWarSystemTest {
   private static final int NUM_GENERATIONS = 100;
   private static final Type[] VARIABLE_TYPES = createTypeArray(5);
   private static final int NUM_CONSTANTS = 5;

   @Test
   public void test() {
      SystemTestConfig config = new SystemTestConfig();
      config.setReturnType(integerType());
      config.setTerminator(createTerminator());
      config.useIntegerConstants(NUM_CONSTANTS);
      config.setVariables(VARIABLE_TYPES);
      config.setFunctionSet(IntegerUtils.INTEGER_UTILS.getAdd(), IntegerUtils.INTEGER_UTILS.getSubtract(), IntegerUtils.INTEGER_UTILS.getMultiply(),
            new LessThan(integerType()), new LessThanOrEqual(integerType()), new GreaterThan(integerType()), new GreaterThanOrEqual(integerType()), new Equal(
                  integerType()), new NotEqual(integerType()), new If());
      config.setGenerationProcessor(new RoundRobinTournament(createGridWarGame()));
      config.process();
   }

   private TwoPlayerGame createGridWarGame() {
      TwoPlayerGame game = new FirstPlayerAdvantageGame(new GridWar(RANDOM));
      return new TwoPlayerGameCache(GENERATION_SIZE * 2, game);
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
