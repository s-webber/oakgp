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
package org.oakgp.function.classify;

import org.oakgp.function.AbstractFunctionTest;

public class IsOddTest extends AbstractFunctionTest {
   @Override
   protected IsOdd getFunction() {
      return IsOdd.getSingleton();
   }

   @Override
   public void testEvaluate() {
      evaluate("(odd? 28)").to(false);
      evaluate("(odd? 27)").to(true);
      evaluate("(odd? 2)").to(false);
      evaluate("(odd? 1)").to(true);
      evaluate("(odd? 0)").to(false);
      evaluate("(odd? -1)").to(true);
      evaluate("(odd? -2)").to(false);
      evaluate("(odd? -27)").to(true);
      evaluate("(odd? -28)").to(false);
   }

   @Override
   public void testCanSimplify() {
      simplify("(odd? 1)").to("true");
      simplify("(odd? 2)").to("false");
   }

   @Override
   public void testCannotSimplify() {
   }
}
