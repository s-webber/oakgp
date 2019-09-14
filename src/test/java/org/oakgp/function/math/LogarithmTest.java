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
package org.oakgp.function.math;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class LogarithmTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new Logarithm();
   }

   @Override
   public void testEvaluate() {
      evaluate("(log 7.5)").to(2.0149030205422647);
   }

   @Override
   public void testCanSimplify() {
   }

   @Override
   public void testCannotSimplify() {
   }
}
