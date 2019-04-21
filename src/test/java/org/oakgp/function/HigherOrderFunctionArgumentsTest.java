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
package org.oakgp.function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class HigherOrderFunctionArgumentsTest {
   @Test
   public void testSingleArg() {
      Object singleArg = new Object();

      HigherOrderFunctionArguments args = new HigherOrderFunctionArguments(singleArg);

      assertEquals(1, args.size());
      assertSame(singleArg, args.first());
      assertSame(singleArg, args.getArg(0));
   }

   @Test
   public void testTwoArgs() {
      Object first = new Object();
      Object second = new Object();

      HigherOrderFunctionArguments args = new HigherOrderFunctionArguments(first, second);

      assertEquals(2, args.size());
      assertSame(first, args.first());
      assertSame(first, args.getArg(0));
      assertSame(second, args.second());
      assertSame(second, args.getArg(1));
   }
}
