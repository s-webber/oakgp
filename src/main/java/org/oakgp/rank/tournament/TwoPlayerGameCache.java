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

import java.util.Map;

import org.oakgp.node.Node;
import org.oakgp.util.CacheMap;

/** Wraps a {@code TwoPlayerGame} to provide caching of results. */
public final class TwoPlayerGameCache implements TwoPlayerGame {
   private final TwoPlayerGame twoPlayerGame;
   private final Map<NodePair, TwoPlayerGameResult> cache;

   /** Creates a cache of the given maximum size which will contain the results of evaluating the given {@code TwoPlayerGame}. */
   public TwoPlayerGameCache(int maxSize, TwoPlayerGame twoPlayerGame) {
      this.twoPlayerGame = twoPlayerGame;
      this.cache = CacheMap.createCache(maxSize);
   }

   @Override
   public TwoPlayerGameResult evaluate(Node player1, Node player2) {
      NodePair pair = new NodePair(player1, player2);
      TwoPlayerGameResult result = cache.get(pair);
      if (result == null) {
         result = twoPlayerGame.evaluate(player1, player2);
         cache.put(new NodePair(player2, player1), result.flip());
         cache.put(pair, result);
      }
      return result;
   }

   private static class NodePair {
      private final Node n1;
      private final Node n2;
      private final int hashCode;

      public NodePair(Node n1, Node n2) {
         this.n1 = n1;
         this.n2 = n2;
         this.hashCode = (n1.hashCode() * 31) * n2.hashCode();
      }

      @Override
      public boolean equals(Object o) {
         NodePair p = (NodePair) o;
         return n1.equals(p.n1) && n2.equals(p.n2);
      }

      @Override
      public int hashCode() {
         return hashCode;
      }
   }
}
