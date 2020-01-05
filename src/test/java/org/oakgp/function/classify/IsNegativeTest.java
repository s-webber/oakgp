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
package org.oakgp.function.classify;

import org.oakgp.function.AbstractFunctionTest;

public class IsNegativeTest extends AbstractFunctionTest {
   @Override
   protected IsNegative getFunction() {
      return IsNegative.getSingleton();
   }

   @Override
   public void testEvaluate() {
      evaluate("(neg? -27)").to(true);
      evaluate("(neg? -1)").to(true);
      evaluate("(neg? 0)").to(false);
      evaluate("(neg? 1)").to(false);
      evaluate("(neg? 27)").to(false);
   }

   @Override
   public void testCanSimplify() {
      simplify("(neg? -1)").to("true");
   }

   @Override
   public void testCannotSimplify() {
   }
}
