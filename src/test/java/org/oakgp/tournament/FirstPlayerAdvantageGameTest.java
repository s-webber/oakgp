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
package org.oakgp.tournament;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.oakgp.TestUtils.integerConstant;

import org.junit.Test;
import org.oakgp.node.Node;

public class FirstPlayerAdvantageGameTest {
   @Test
   public void test() {
      Node n1 = integerConstant(1);
      Node n2 = integerConstant(2);
      TwoPlayerGame mockTwoPlayerGame = mock(TwoPlayerGame.class);
      given(mockTwoPlayerGame.evaluate(n1, n2)).willReturn(7d);
      given(mockTwoPlayerGame.evaluate(n2, n1)).willReturn(4d);

      FirstPlayerAdvantageGame g = new FirstPlayerAdvantageGame(mockTwoPlayerGame);

      assertEquals(3d, g.evaluate(n1, n2), 0);
      assertEquals(-3d, g.evaluate(n2, n1), 0);
   }
}
