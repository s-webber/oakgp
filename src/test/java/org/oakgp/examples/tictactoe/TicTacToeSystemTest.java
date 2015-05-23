package org.oakgp.examples.tictactoe;

import static org.oakgp.Type.type;
import static org.oakgp.examples.SystemTestConfig.GENERATION_SIZE;

import java.util.List;
import java.util.function.Predicate;

import org.junit.Test;
import org.oakgp.Assignments;
import org.oakgp.RankedCandidate;
import org.oakgp.TestUtils;
import org.oakgp.Type;
import org.oakgp.examples.SystemTestConfig;
import org.oakgp.fitness.FitnessFunction;
import org.oakgp.fitness.FitnessFunctionGenerationProcessor;
import org.oakgp.function.choice.If;
import org.oakgp.function.choice.OrElse;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.tournament.FirstPlayerAdvantageGame;
import org.oakgp.tournament.RoundRobinTournament;
import org.oakgp.tournament.TwoPlayerGame;
import org.oakgp.tournament.TwoPlayerGameCache;
import org.oakgp.util.DummyNode;

public class TicTacToeSystemTest {
   private static final int NUM_GENERATIONS = 10;
   private static final Type BOARD_TYPE = type("board");
   private static final Type MOVE_TYPE = type("move");
   private static final Type POSSIBLE_MOVE = type("possibleMove");
   private static final Type SYMBOL_TYPE = type("symbol");

   @Test
   public void testHighLevel() {
      SystemTestConfig config = new SystemTestConfig();
      config.setConstants();
      config.setReturnType(MOVE_TYPE);
      config.setVariables(BOARD_TYPE, SYMBOL_TYPE, SYMBOL_TYPE);
      config.setTerminator(createTerminator());
      config.setFunctionSet(new GetPossibleMove("corner", Board::getFreeCorner), new GetPossibleMove("centre", Board::getFreeCentre), new GetPossibleMove(
            "side", Board::getFreeSide), new GetWinningMove(), new GetAnyMove(), new OrElse(MOVE_TYPE));
      config.setGenerationProcessor(new RoundRobinTournament(createTicTacToeGame()));
      config.process();
   }

   @Test
   public void testLowLevelTournament() {
      SystemTestConfig config = new SystemTestConfig();
      config.setConstants(getMoveConstants());
      config.setReturnType(MOVE_TYPE);
      config.setVariables(BOARD_TYPE, SYMBOL_TYPE, SYMBOL_TYPE);
      config.setTerminator(createTerminator());
      config.setFunctionSet(new IsFree(), new IsOccupied(), new GetAnyMove(), new IfValidMove(), new OrElse(MOVE_TYPE), new And(), new If(POSSIBLE_MOVE));
      config.setGenerationProcessor(new RoundRobinTournament(createTicTacToeGame()));
      config.process();
   }

   @Test
   public void testLowLevelFitnessFunction() {
      SystemTestConfig config = new SystemTestConfig();
      config.setConstants(getMoveConstants());
      config.setReturnType(MOVE_TYPE);
      config.setVariables(BOARD_TYPE, SYMBOL_TYPE, SYMBOL_TYPE);
      config.setTerminator(createTerminator());
      config.setFunctionSet(new IsFree(), new IsOccupied(), new GetAnyMove(), new IfValidMove(), new OrElse(MOVE_TYPE), new And(), new If(POSSIBLE_MOVE));
      TicTacToeFitnessFunction fitnessFunction = new TicTacToeFitnessFunction();
      FitnessFunctionGenerationProcessor generationProcessor = new FitnessFunctionGenerationProcessor(fitnessFunction);
      config.setGenerationProcessor(generationProcessor);
      Node result = config.process();
      System.out.println(fitnessFunction.evaluate(result));
   }

   private ConstantNode[] getMoveConstants() {
      return TestUtils.createEnumConstants(Move.class, POSSIBLE_MOVE);
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
               System.out.println(ctr + " " + t.iterator().next());
            }
            return ctr++ > NUM_GENERATIONS;
         }
      };
   }

   private class TicTacToeFitnessFunction implements FitnessFunction {
      private TicTacToe ticTacToe = new TicTacToe();
      private Node[] ais = new Node[] {//
      new DummyNode() {
         @Override
         public Move evaluate(Assignments assignments) {
            Board board = (Board) assignments.get(0);
            return board.getFreeMove();
         }
      }, new DummyNode() {
         @Override
         public Move evaluate(Assignments assignments) {
            Board board = (Board) assignments.get(0);
            Move nextMove = board.getWinningMove(Symbol.X);
            if (nextMove == null) {
               nextMove = board.getFreeMove();
            }
            return nextMove;
         }
      }, new DummyNode() {
         @Override
         public Move evaluate(Assignments assignments) {
            Board board = (Board) assignments.get(0);
            Move nextMove = board.getWinningMove(Symbol.O);
            if (nextMove == null) {
               nextMove = board.getFreeMove();
            }
            return nextMove;
         }
      }, new DummyNode() {
         @Override
         public Move evaluate(Assignments assignments) {
            Board board = (Board) assignments.get(0);
            Move nextMove = board.getWinningMove(Symbol.X);
            if (nextMove == null) {
               nextMove = board.getWinningMove(Symbol.O);
            }
            if (nextMove == null) {
               nextMove = board.getFreeCorner();
            }
            if (nextMove == null) {
               nextMove = board.getFreeCentre();
            }
            if (nextMove == null) {
               nextMove = board.getFreeMove();
            }
            return nextMove;
         }
      }, new DummyNode() {
         @Override
         public Move evaluate(Assignments assignments) {
            Board board = (Board) assignments.get(0);
            Move nextMove = board.getWinningMove(Symbol.X);
            if (nextMove == null) {
               nextMove = board.getWinningMove(Symbol.O);
            }
            if (nextMove == null) {
               nextMove = board.getFreeCorner();
            }
            if (nextMove == null) {
               nextMove = board.getFreeCentre();
            }
            if (nextMove == null) {
               nextMove = board.getFreeMove();
            }
            return nextMove;
         }
      } };

      @Override
      public double evaluate(Node candidate) {
         int result = 0;
         for (Node ai : ais) {
            result += ticTacToe.evaluate(ai, candidate);
         }
         return result;
      }
   }
}
