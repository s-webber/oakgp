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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cards { // TODO
   private static final List<Card> CARDS = new ArrayList<>();
   static {
      for (Suit suit : Suit.values()) {
         for (Rank rank : Rank.values()) {
            CARDS.add(new Card(rank, suit));
         }
      }
   }

   static List<Card> deal() {
      List<Card> deck = new ArrayList<>(CARDS);
      Collections.shuffle(deck);
      return deck;
   }
}
