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

import org.oakgp.rank.RankedCandidate;
import org.oakgp.rank.RankedCandidates;

/**
 * A predicate that returns {@code true} when a candidate has been found that fulfils the specified criteria.
 * <p>
 * Also known as the <i>success predicate</i>.
 */
public final class TargetFitnessTerminator implements Predicate<RankedCandidates> {
   private final Predicate<RankedCandidate> targetCriteira;

   /** Creates a predicate which returns the result of evaluating the given criteria with the best candidate of a {@code RankedCandidates}. */
   public TargetFitnessTerminator(Predicate<RankedCandidate> targetCriteira) {
      this.targetCriteira = targetCriteira;
   }

   @Override
   public boolean test(RankedCandidates t) {
      return targetCriteira.test(t.best());
   }
}
