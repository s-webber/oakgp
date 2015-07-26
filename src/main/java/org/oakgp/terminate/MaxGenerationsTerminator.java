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
package org.oakgp.terminate;

import java.util.function.Predicate;
import java.util.logging.Logger;

import org.oakgp.rank.RankedCandidates;

/**
 * A predicate that returns {@code true} when a specified number of generations has been run.
 * <p>
 * Also known as the <i>generational predicate</i>.
 */
public final class MaxGenerationsTerminator implements Predicate<RankedCandidates> {
   private final int maxGenerations;
   private int ctr = 0;

   /** Constructs a new {@code Predicate} that will return {@code true} once the specified number of generations have been run. */
   public MaxGenerationsTerminator(int maxGenerations) {
      this.maxGenerations = maxGenerations;
   }

   @Override
   public boolean test(RankedCandidates t) {
      if (ctr % 100 == 0) {
         Logger.getGlobal().info("Generation: " + ctr + " Best: " + t.best());
      }
      return ++ctr > maxGenerations;
   }
}
