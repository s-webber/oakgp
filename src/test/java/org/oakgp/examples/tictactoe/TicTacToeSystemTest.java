/*
 * Copyright 2015 S. Webber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oakgp.examples.tictactoe;

import static org.oakgp.type.Types.declareType;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;
import org.oakgp.Assignments;
import org.oakgp.NodeSimplifier;
import org.oakgp.function.bool.And;
import org.oakgp.function.choice.If;
import org.oakgp.function.choice.OrElse;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.primitive.VariableSet;
import org.oakgp.rank.fitness.FitnessFunction;
import org.oakgp.rank.tournament.FirstPlayerAdvantageGame;
import org.oakgp.rank.tournament.TwoPlayerGame;
import org.oakgp.serialize.NodeReader;
import org.oakgp.type.Types.Type;
import org.oakgp.util.DummyNode;
import org.oakgp.util.FunctionSetBuilder;
import org.oakgp.util.RunBuilder;
import org.oakgp.util.Utils;

public class TicTacToeSystemTest {
   private static final int NUM_GENERATIONS = 10;
   private static final int INITIAL_POPULATION_SIZE = 50;
   private static final int INITIAL_POPULATION_MAX_DEPTH = 4;
   static final Type BOARD_TYPE = declareType("board");
   static final Type MOVE_TYPE = declareType("tictactoemove");
   static final Type POSSIBLE_MOVE = declareType("possibleMove");
   static final Type SYMBOL_TYPE = declareType("symbol");
   private static final Type[] VARIABLE_TYPES = { BOARD_TYPE, SYMBOL_TYPE, SYMBOL_TYPE };

   @Test
   public void testHighLevel() {
      FunctionSet functionSet = new FunctionSetBuilder().addAll(new GetPossibleMove("corner", Board::getFreeCorner),
            new GetPossibleMove("centre", Board::getFreeCentre), new GetPossibleMove("side", Board::getFreeSide), new GetWinningMove(), new GetAnyMove())
            .add(new OrElse(), MOVE_TYPE).build();
      TwoPlayerGame game = createTicTacToeGame();

      new RunBuilder().setReturnType(MOVE_TYPE).setConstants().setVariables(VARIABLE_TYPES).setFunctionSet(functionSet).setTwoPlayerGame(game)
            .setInitialPopulationSize(INITIAL_POPULATION_SIZE).setTreeDepth(INITIAL_POPULATION_MAX_DEPTH).setMaxGenerations(NUM_GENERATIONS).process();
   }

   @Test
   public void testLowLevelTournament() {
      FunctionSet functionSet = new FunctionSetBuilder().addAll(new IsFree(), new IsOccupied(), new GetAnyMove(), new IfValidMove(), And.getSingleton())
            .add(new OrElse(), MOVE_TYPE).add(new If(), POSSIBLE_MOVE).build();
      Collection<ConstantNode> constants = getMoveConstants();
      TwoPlayerGame game = createTicTacToeGame();

      new RunBuilder().setReturnType(MOVE_TYPE).setConstants(constants).setVariables(VARIABLE_TYPES).setFunctionSet(functionSet).setTwoPlayerGame(game)
            .setInitialPopulationSize(INITIAL_POPULATION_SIZE).setTreeDepth(INITIAL_POPULATION_MAX_DEPTH).setMaxGenerations(NUM_GENERATIONS).process();
   }

   @Test
   public void temp() throws IOException { // TODO remove
      FunctionSet functionSet = new FunctionSetBuilder().addAll(new IsFree(), new IsOccupied(), new GetAnyMove(), new IfValidMove(), And.getSingleton())
            .add(new OrElse(), MOVE_TYPE).add(new If(), POSSIBLE_MOVE).build();
      Collection<ConstantNode> constants = getMoveConstants();

      String y = "(or-else (if-valid-move v0 (if (occupied? v0 BOTTOM_CENTRE v2) (if (and (and (occupied? v0 MIDDLE_LEFT v1) (and (free? v0 TOP_CENTRE) (free? v0 BOTTOM_LEFT))) (and (occupied? v0 TOP_LEFT v2) (and (and (free? v0 TOP_RIGHT) (and (and (and (and (and (and (and (occupied? v0 MIDDLE_LEFT v1) (occupied? v0 BOTTOM_LEFT v2)) (occupied? v0 BOTTOM_LEFT v1)) (free? v0 MIDDLE_RIGHT)) (occupied? v0 TOP_LEFT v2)) (and (free? v0 BOTTOM_RIGHT) (and (and (free? v0 BOTTOM_LEFT) (and (occupied? v0 TOP_LEFT v1) (occupied? v0 BOTTOM_CENTRE v1))) (and (free? v0 TOP_LEFT) (and (occupied? v0 TOP_RIGHT v1) (free? v0 TOP_LEFT)))))) (occupied? v0 MIDDLE_LEFT v1)) (free? v0 BOTTOM_LEFT))) (occupied? v0 BOTTOM_RIGHT v2)))) CENTRE TOP_CENTRE) TOP_CENTRE)) (any v0))";
      String x = "(and (occupied? v0 MIDDLE_LEFT v1) (and (occupied? v0 BOTTOM_LEFT v2) (and (occupied? v0 BOTTOM_RIGHT v2) (and (free? v0 BOTTOM_LEFT) (and (occupied? v0 TOP_LEFT v2) (and (free? v0 TOP_LEFT) (and (free? v0 TOP_CENTRE) (and (free? v0 TOP_RIGHT) (and (free? v0 MIDDLE_RIGHT) (and (occupied? v0 BOTTOM_LEFT v1) (and (occupied? v0 TOP_LEFT v1) (and (free? v0 BOTTOM_RIGHT) (and (occupied? v0 BOTTOM_CENTRE v1) (occupied? v0 TOP_RIGHT v1))))))))))))))";
      String z = "(if (and (and (occupied? v0 MIDDLE_LEFT v1) (and (free? v0 TOP_CENTRE) (free? v0 BOTTOM_LEFT))) (and (occupied? v0 TOP_LEFT v2) (and (and (free? v0 TOP_RIGHT) (and (and (and (and (and (and (and (occupied? v0 MIDDLE_LEFT v1) (occupied? v0 BOTTOM_LEFT v2)) (occupied? v0 BOTTOM_LEFT v1)) (free? v0 MIDDLE_RIGHT)) (occupied? v0 TOP_LEFT v2)) (and (free? v0 BOTTOM_RIGHT) (and (and (free? v0 BOTTOM_LEFT) (and (occupied? v0 TOP_LEFT v1) (occupied? v0 BOTTOM_CENTRE v1))) (and (free? v0 TOP_LEFT) (and (occupied? v0 TOP_RIGHT v1) (free? v0 TOP_LEFT)))))) (occupied? v0 MIDDLE_LEFT v1)) (free? v0 BOTTOM_LEFT))) (occupied? v0 BOTTOM_RIGHT v2)))) CENTRE TOP_CENTRE)";
      String w = "(and (occupied? v0 TOP_LEFT v2) (and (occupied? v0 BOTTOM_LEFT v2) (and (free? v0 TOP_CENTRE) (and (free? v0 TOP_LEFT) (and (free? v0 TOP_RIGHT) (and (free? v0 BOTTOM_LEFT) (and (occupied? v0 BOTTOM_RIGHT v2) (and (free? v0 MIDDLE_RIGHT) (and (free? v0 BOTTOM_RIGHT) (and (occupied? v0 TOP_LEFT v1) (and (occupied? v0 BOTTOM_LEFT v1) (and (occupied? v0 BOTTOM_CENTRE v1) (and (occupied? v0 TOP_RIGHT v1) (occupied? v0 MIDDLE_LEFT v1))))))))))))))";
      String q = "(and (occupied? v0 CENTRE v2) (and (occupied? v0 MIDDLE_LEFT v2) (and (occupied? v0 TOP_LEFT v1) (and (free? v0 CENTRE) (and (occupied? v0 BOTTOM_RIGHT v1) (and (occupied? v0 BOTTOM_LEFT v2) (and (occupied? v0 MIDDLE_RIGHT v1) (and (free? v0 MIDDLE_LEFT) (and (occupied? v0 TOP_CENTRE v2) (and (free? v0 MIDDLE_RIGHT) (and (free? v0 BOTTOM_RIGHT) (and (free? v0 BOTTOM_LEFT) (and (free? v0 TOP_RIGHT) (and (free? v0 TOP_CENTRE) (and (occupied? v0 CENTRE v1) (and (occupied? v0 MIDDLE_LEFT v1) (occupied? v0 BOTTOM_CENTRE v2)))))))))))))))))";
      String q2 = "(and (free? v0 CENTRE) (and (occupied? v0 MIDDLE_LEFT v1) (and (free? v0 MIDDLE_LEFT) (and (free? v0 BOTTOM_RIGHT) (and (occupied? v0 BOTTOM_LEFT v2) (and (occupied? v0 CENTRE v2) (and (free? v0 BOTTOM_LEFT) (and (occupied? v0 TOP_CENTRE v2) (and (occupied? v0 MIDDLE_LEFT v2) (and (free? v0 MIDDLE_RIGHT) (and (free? v0 TOP_CENTRE) (and (occupied? v0 BOTTOM_RIGHT v1) (and (occupied? v0 TOP_LEFT v1) (and (free? v0 TOP_RIGHT) (and (occupied? v0 CENTRE v1) (and (occupied? v0 BOTTOM_CENTRE v2) (occupied? v0 MIDDLE_RIGHT v1)))))))))))))))))";
      NodeReader r = new NodeReader(q2, functionSet, constants.toArray(new ConstantNode[0]), VariableSet.createVariableSet(VARIABLE_TYPES));
      Node in = r.readNode();
      long now = System.currentTimeMillis();
      Node out = NodeSimplifier.simplify(in);
      System.out.println("took " + (System.currentTimeMillis() - now));
      System.out.println(">> " + in.getNodeCount() + " " + in);
      System.out.println("<< " + out.getNodeCount() + " " + out);
      now = System.currentTimeMillis();
      Node out2 = NodeSimplifier.simplify(out);
      System.out.println("took " + (System.currentTimeMillis() - now));
      System.out.println("<< " + out2.getNodeCount() + " " + out2);
      now = System.currentTimeMillis();
      Node out3 = NodeSimplifier.simplify(out);
      System.out.println("took " + (System.currentTimeMillis() - now));
      System.out.println("<< " + out3.getNodeCount() + " " + out3);
   }

   @Test(timeout = 5000)
   public void testLowLevelFitnessFunction() {
      FunctionSet functionSet = new FunctionSetBuilder().addAll(new IsFree(), new IsOccupied(), new GetAnyMove(), new IfValidMove(), And.getSingleton())
            .add(new OrElse(), MOVE_TYPE).add(new If(), POSSIBLE_MOVE).build();
      Collection<ConstantNode> constants = getMoveConstants();
      TicTacToeFitnessFunction fitnessFunction = new TicTacToeFitnessFunction();

      new RunBuilder().setReturnType(MOVE_TYPE).setConstants(constants).setVariables(VARIABLE_TYPES).setFunctionSet(functionSet)
            .setFitnessFunction(fitnessFunction).setInitialPopulationSize(INITIAL_POPULATION_SIZE).setTreeDepth(INITIAL_POPULATION_MAX_DEPTH)
            .setMaxGenerations(NUM_GENERATIONS).process();
   }

   private Collection<ConstantNode> getMoveConstants() {
      return Utils.createEnumConstants(Move.class, POSSIBLE_MOVE);
   }

   private TwoPlayerGame createTicTacToeGame() {
      return new FirstPlayerAdvantageGame(new TicTacToe());
   }

   private class TicTacToeFitnessFunction implements FitnessFunction {
      private TicTacToe ticTacToe = new TicTacToe();
      private Node[] ais = new Node[] { //
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
            result += ticTacToe.evaluate(ai, candidate).getFitness1();
         }
         return result;
      }
   }
}
