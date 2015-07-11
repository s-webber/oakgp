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
package org.oakgp.rank.tournament;

import org.oakgp.node.Node;

/**
 * Some games have a "first-mover advantage" - meaning the player that moves first has a greater chance of winning than the player that moves second. To avoid
 * "first-mover advantage" causing players to be unfairly ranked, each pair plays each other twice - with each having the opportunity to move first.
 */
public final class FirstPlayerAdvantageGame implements TwoPlayerGame {
   private final TwoPlayerGame twoPlayerGame;

   public FirstPlayerAdvantageGame(TwoPlayerGame twoPlayerGame) {
      this.twoPlayerGame = twoPlayerGame;
   }

   @Override
   public double evaluate(Node player1, Node player2) {
      return twoPlayerGame.evaluate(player1, player2) - twoPlayerGame.evaluate(player2, player1);
   }
}
