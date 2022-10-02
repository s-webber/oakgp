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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.oakgp.Assignments;
import org.oakgp.node.Node;
import org.oakgp.rank.fitness.FitnessFunction;

class PokerFitnessFunction implements FitnessFunction {
   private static final int CARDS_PER_HAND = 5;
   private final Map<List<Card>, Category> testCases;

   public PokerFitnessFunction() {
      testCases = new LinkedHashMap<>();
      for (int i = 0; i < 10000; i++) {
         testCases.computeIfAbsent(createHand(), h -> {
            Collection<Long> rankCount = h.stream().collect(Collectors.groupingBy(Card::getRank, Collectors.counting())).values();
            Collection<Long> suitCount = h.stream().collect(Collectors.groupingBy(Card::getSuit, Collectors.counting())).values();
            boolean isSequential = isSequential(h, rankCount);
            boolean isFlush = suitCount.size() == 1;
            boolean isThreeOfAKind = rankCount.contains(3L);
            boolean isTwoOfAKind = rankCount.contains(2L);
            if (isSequential && isFlush) {
               return Category.STRAIGHT_FLUSH;
            } else if (rankCount.contains(4L)) {
               return Category.FOUR_OF_A_KIND;
            } else if (isThreeOfAKind && isTwoOfAKind) {
               return Category.FULL_HOUSE;
            } else if (isFlush) {
               return Category.FLUSH;
            } else if (isSequential) {
               return Category.STRAIGHT;
            } else if (isThreeOfAKind) {
               return Category.THREE_OF_A_KIND;
            } else if (rankCount.size() == 3) {
               return Category.TWO_PAIR;
            } else if (isTwoOfAKind) {
               return Category.PAIR;
            } else {
               return Category.HIGH_CARD;
            }
         });
      }
   }

   private static List<Card> createHand() {
      Iterator<Card> pack = Cards.deal().iterator();

      List<Card> hand = new ArrayList<>();
      for (int i = 0; i < CARDS_PER_HAND; i++) {
         hand.add(pack.next());
      }
      return hand;
   }

   private static boolean isSequential(List<Card> h, Collection<Long> rankCount) {
      if (rankCount.size() == CARDS_PER_HAND) {
         int minAceLow = Integer.MAX_VALUE;
         int minAceHigh = Integer.MAX_VALUE;
         int maxAceLow = Integer.MIN_VALUE;
         int maxAceHigh = Integer.MIN_VALUE;
         for (Card c : h) {
            minAceLow = Math.min(minAceLow, c.getRank().low());
            maxAceLow = Math.max(maxAceLow, c.getRank().low());
            minAceHigh = Math.min(minAceHigh, c.getRank().high());
            maxAceHigh = Math.max(maxAceHigh, c.getRank().high());
         }
         return (maxAceLow - minAceLow == 4) || (maxAceHigh - minAceHigh == 4);
      }
      return false;
   }

   @Override
   public double evaluate(Node n) {
      double fitness = 0;
      for (Map.Entry<List<Card>, Category> testCase : testCases.entrySet()) {
         Category category = n.evaluate(Assignments.createAssignments(testCase.getKey()), null);
         if (category != testCase.getValue()) {
            fitness++;
         }
      }
      return fitness;
   }
}
