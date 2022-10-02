/*
 * Copyright 2019 S. Webber
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
package org.oakgp.examples.poker;

enum Category {
   /** Straight flush: All cards in the same suit, and in sequence */
   STRAIGHT_FLUSH,
   /** Four of a kind: Four of the cards have the same rank */
   FOUR_OF_A_KIND,
   /** Full House: Three cards of one rank, the other two of another rank */
   FULL_HOUSE,
   /** Flush: All cards in the same suit */
   FLUSH,
   /** Straight: All cards in sequence (aces can be high or low, but not both at once) */
   STRAIGHT,
   /** Three of a kind: Three of the cards have the same rank */
   THREE_OF_A_KIND,
   /** Two pair: Two pairs of cards have the same rank */
   TWO_PAIR,
   /** Pair: Two cards have the same rank */
   PAIR,
   /** High card: None of the above conditions are met */
   HIGH_CARD
}
