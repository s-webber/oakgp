package org.oakgp.examples.tictactoe;

import static org.oakgp.Type.type;

import org.junit.Test;
import org.oakgp.Assignments;
import org.oakgp.Type;
import org.oakgp.fitness.FitnessFunction;
import org.oakgp.function.Function;
import org.oakgp.function.choice.If;
import org.oakgp.function.choice.OrElse;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.tournament.FirstPlayerAdvantageGame;
import org.oakgp.tournament.TwoPlayerGame;
import org.oakgp.util.DummyNode;
import org.oakgp.util.RunBuilder;
import org.oakgp.util.Utils;

public class TicTacToeSystemTest {
   private static final int NUM_GENERATIONS = 10;
   private static final int INITIAL_GENERATION_SIZE = 50;
   private static final int INITIAL_GENERATION_MAX_DEPTH = 4;
   private static final Type BOARD_TYPE = type("board");
   private static final Type MOVE_TYPE = type("move");
   private static final Type POSSIBLE_MOVE = type("possibleMove");
   private static final Type SYMBOL_TYPE = type("symbol");
   private static final Type[] VARIABLE_TYPES = { BOARD_TYPE, SYMBOL_TYPE, SYMBOL_TYPE };

   @Test
   public void testHighLevel() {
      Function[] functions = { new GetPossibleMove("corner", Board::getFreeCorner), new GetPossibleMove("centre", Board::getFreeCentre),
            new GetPossibleMove("side", Board::getFreeSide), new GetWinningMove(), new GetAnyMove(), new OrElse(MOVE_TYPE) };
      TwoPlayerGame game = createTicTacToeGame();

      new RunBuilder().setReturnType(MOVE_TYPE).setConstants().setVariables(VARIABLE_TYPES).setFunctionSet(functions).setTwoPlayerGame(game)
            .setInitialGenerationSize(INITIAL_GENERATION_SIZE).setTreeDepth(INITIAL_GENERATION_MAX_DEPTH).setMaxGenerations(NUM_GENERATIONS).process();
   }

   @Test
   public void testLowLevelTournament() {
      Function[] functions = { new IsFree(), new IsOccupied(), new GetAnyMove(), new IfValidMove(), new OrElse(MOVE_TYPE), new And(), new If(POSSIBLE_MOVE) };
      ConstantNode[] constants = getMoveConstants();
      TwoPlayerGame game = createTicTacToeGame();

      new RunBuilder().setReturnType(MOVE_TYPE).setConstants(constants).setVariables(VARIABLE_TYPES).setFunctionSet(functions).setTwoPlayerGame(game)
            .setInitialGenerationSize(INITIAL_GENERATION_SIZE).setTreeDepth(INITIAL_GENERATION_MAX_DEPTH).setMaxGenerations(NUM_GENERATIONS).process();
   }

   @Test
   public void testLowLevelFitnessFunction() {
      Function[] functions = { new IsFree(), new IsOccupied(), new GetAnyMove(), new IfValidMove(), new OrElse(MOVE_TYPE), new And(), new If(POSSIBLE_MOVE) };
      ConstantNode[] constants = getMoveConstants();
      TicTacToeFitnessFunction fitnessFunction = new TicTacToeFitnessFunction();

      new RunBuilder().setReturnType(MOVE_TYPE).setConstants(constants).setVariables(VARIABLE_TYPES).setFunctionSet(functions)
            .setFitnessFunction(fitnessFunction).setInitialGenerationSize(INITIAL_GENERATION_SIZE).setTreeDepth(INITIAL_GENERATION_MAX_DEPTH)
            .setMaxGenerations(NUM_GENERATIONS).process();
   }

   private ConstantNode[] getMoveConstants() {
      return Utils.createEnumConstants(Move.class, POSSIBLE_MOVE);
   }

   private TwoPlayerGame createTicTacToeGame() {
      return new FirstPlayerAdvantageGame(new TicTacToe());
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
