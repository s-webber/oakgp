/*
 * Copyright 2022 S. Webber
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
package org.oakgp.minimax;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.AbstractDefinedFunctions;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.rank.tournament.TwoPlayerGameResult;
import org.oakgp.type.CommonTypes;

public class MinimaxTest {
   @Test
   public void test() {
      Function f1 = new Function() { // TODO add dummy function that takes function in constructor
         @Override
         public Signature getSignature() {
            throw new UnsupportedOperationException();
         }

         @Override
         public Object evaluate(ChildNodes arguments, Assignments assignments, AbstractDefinedFunctions adfs) {
            TicTacToe state = (TicTacToe) assignments.get(0);
            return (int) state.getResult().getFitness1();
         }
      };
      Function f2 = new Function() { // TODO add dummy function that takes function in constructor
         @Override
         public Signature getSignature() {
            throw new UnsupportedOperationException();
         }

         @Override
         public Object evaluate(ChildNodes arguments, Assignments assignments, AbstractDefinedFunctions adfs) {
            TicTacToe state = (TicTacToe) assignments.get(0);
            return (int) state.getResult().getFitness2();
         }
      };
      MinimaxFunction f = new MinimaxFunction(-1);

      TwoPlayerGameResult result = new MinimaxGame(TicTacToe::new).evaluate(
            new FunctionNode(f, f.getSignature().getReturnType(), new FunctionNode(f1, CommonTypes.integerType())),
            new FunctionNode(f, f.getSignature().getReturnType(), new FunctionNode(f2, CommonTypes.integerType())));
      System.out.println(result);
   }
}

class TicTacToe implements MinimaxGameState {
   private static final TwoPlayerGameResult PLAYER_1_WINS = new TwoPlayerGameResult(1000, -1000);
   private static final TwoPlayerGameResult PLAYER_2_WINS = new TwoPlayerGameResult(1000, -1000);
   private static final TwoPlayerGameResult NO_WINNER = new TwoPlayerGameResult(0, 0);
   private static final MinimaxGameState[] GAME_OVER = new MinimaxGameState[0];
   private static long BOARD = 0b111_111_111;
   private static long[] LINES = { 0b111, 0b111_000, 0b111_000_000, 0b100_100_100, 0b10_010_010, 0b1_001_001, 0b100_010_001, 0b1_010_100 };

   private final long player1;
   private final long player2;
   private final boolean player1Move;

   public TicTacToe() {
      this(0, 0, true);
   }

   TicTacToe(long player1, long player2, boolean player1Move) {
      this.player1 = player1;
      this.player2 = player2;
      this.player1Move = player1Move;
   }

   @Override
   public MinimaxGameState[] getChildren() {
      long remaining = BOARD - player1 - player2;
      if (remaining == 0 || getResult() != NO_WINNER) {
         return GAME_OVER;
      }

      List<MinimaxGameState> moves = new ArrayList<>();
      for (long i = 0, b = 1; i < 9; i++, b *= 2) {
         if ((remaining & b) == b) {
            if (player1Move) {
               moves.add(new TicTacToe(player1 + b, player2, false));
            } else {
               moves.add(new TicTacToe(player1, player2 + b, true));
            }
         }
      }
      return moves.toArray(new MinimaxGameState[moves.size()]);
   }

   @Override
   public TwoPlayerGameResult getResult() {
      for (long line : LINES) {
         if ((line & player1) == line) {
            return PLAYER_1_WINS;
         }
      }
      for (long line : LINES) {
         if ((line & player2) == line) {
            return PLAYER_2_WINS;
         }
      }
      return NO_WINNER;
   }

   @Override
   public String toString() {
      return "TicTacToe [player1=" + Long.toBinaryString(player1) + ", player2=" + Long.toBinaryString(player2) + ", player1Move=" + player1Move + "]";
   }
}
