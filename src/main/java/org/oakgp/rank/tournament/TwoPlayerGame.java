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

/** Represents a two-player zero-sum game. */
public interface TwoPlayerGame {
   /**
    * Determines the outcome of the two specified players competing against each other.
    *
    * @param player1
    *           represents the game-logic of a player participating in the game
    * @param player2
    *           represents the game-logic of the other player participating in the game
    * @return the outcome of the game from the point-of-view of {@code player1}, as implementations of {@code TwoPlayerGame} represent zero-sum games then the
    *         corresponding outcome for {@code player2} can be determined by negating this value
    */
   double evaluate(Node player1, Node player2);
}
