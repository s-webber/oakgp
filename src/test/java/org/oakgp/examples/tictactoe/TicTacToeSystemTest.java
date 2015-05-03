package org.oakgp.examples.tictactoe;

import static org.oakgp.Type.type;
import static org.oakgp.examples.SystemTestConfig.GENERATION_SIZE;

import java.util.List;
import java.util.function.Predicate;

import org.junit.Test;
import org.oakgp.RankedCandidate;
import org.oakgp.Type;
import org.oakgp.examples.SystemTestConfig;
import org.oakgp.function.choice.OrElse;
import org.oakgp.tournament.FirstPlayerAdvantageGame;
import org.oakgp.tournament.RoundRobinTournament;
import org.oakgp.tournament.TwoPlayerGame;
import org.oakgp.tournament.TwoPlayerGameCache;

public class TicTacToeSystemTest {
   private static final int NUM_GENERATIONS = 10;

   @Test
   public void test() {
      Type returnType = type("move");

      SystemTestConfig config = new SystemTestConfig();
      config.setConstants();
      config.setReturnType(returnType);
      config.setVariables(type("board"), type("symbol"), type("symbol"));
      config.setTerminator(createTerminator());
      config.setFunctionSet(new GetPossibleMove("corner", Board::getFreeCorner), new GetPossibleMove("centre", Board::getFreeCentre), new GetPossibleMove(
            "side", Board::getFreeSide), new GetWinningMove(), new GetAnyMove(), new OrElse(returnType));
      config.setGenerationProcessor(new RoundRobinTournament(createTicTacToeGame()));
      config.process();
   }

   private TwoPlayerGame createTicTacToeGame() {
      TwoPlayerGame game = new FirstPlayerAdvantageGame(new TicTacToe());
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
