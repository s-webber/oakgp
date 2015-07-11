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
package org.oakgp.select;

import static org.junit.Assert.assertSame;
import static org.oakgp.TestUtils.integerConstant;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.oakgp.node.Node;
import org.oakgp.rank.RankedCandidate;
import org.oakgp.util.DummyRandom;

public class WeightedNodeSelectorTest {
   @Test
   public void test() {
      Node first = integerConstant(0);
      Node second = integerConstant(0);
      Node third = integerConstant(0);
      List<RankedCandidate> candidates = Arrays.asList(new RankedCandidate(first, 0), new RankedCandidate(second, 0), new RankedCandidate(third, 0));

      DummyRandom dummyRandom = new DummyRandom(true, false, false, false, true, false, true, true, false, false);

      WeightedNodeSelectorFactory factory = new WeightedNodeSelectorFactory(dummyRandom);
      WeightedNodeSelector selector = factory.getSelector(candidates);

      assertSame(first, selector.next());
      assertSame(third, selector.next());
      assertSame(second, selector.next());
      assertSame(second, selector.next());
      assertSame(first, selector.next());
      assertSame(third, selector.next());

      dummyRandom.assertEmpty();
   }
}
