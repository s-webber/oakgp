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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.oakgp.TestUtils.singletonRankedCandidates;

import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.oakgp.rank.RankedCandidates;

public class CompositeTerminatorTest {
   private final RankedCandidates candidates = singletonRankedCandidates();

   private Predicate<RankedCandidates> t1;
   private Predicate<RankedCandidates> t2;
   private Predicate<RankedCandidates> t3;

   @SuppressWarnings("unchecked")
   @Before
   public void setup() {
      t1 = mock(Predicate.class);
      t2 = mock(Predicate.class);
      t3 = mock(Predicate.class);
   }

   @Test
   public void testContinue() {
      assertContinue();

      verifyExecuted(t1);
      verifyExecuted(t2);
      verifyExecuted(t3);
   }

   @Test
   public void testTerminateFirst() {
      returnTrue(t1);

      assertTerminate();

      verifyExecuted(t1);
      verifyNotExecuted(t2);
      verifyNotExecuted(t3);
   }

   @Test
   public void testTerminateSecond() {
      returnTrue(t2);

      assertTerminate();

      verifyExecuted(t1);
      verifyExecuted(t2);
      verifyNotExecuted(t3);
   }

   @Test
   public void testTerminateThird() {
      returnTrue(t3);

      assertTerminate();

      verifyExecuted(t1);
      verifyExecuted(t2);
      verifyExecuted(t3);
   }

   private void returnTrue(Predicate<RankedCandidates> t) {
      when(t.test(candidates)).thenReturn(true);
   }

   private void assertTerminate() {
      assertTrue(getOutcome());
   }

   private void assertContinue() {
      assertFalse(getOutcome());
   }

   private boolean getOutcome() {
      CompositeTerminator composite = new CompositeTerminator(t1, t2, t3);
      return composite.test(candidates);
   }

   private void verifyExecuted(Predicate<RankedCandidates> t) {
      verifyExecuted(t, 1);
   }

   private void verifyNotExecuted(Predicate<RankedCandidates> t) {
      verifyExecuted(t, 0);
   }

   private void verifyExecuted(Predicate<RankedCandidates> t, int times) {
      verify(t, times(times)).test(candidates);
   }

}
