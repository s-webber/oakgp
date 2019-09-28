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
package org.oakgp.function.hof;

import static org.oakgp.type.CommonTypes.integerType;

import org.oakgp.function.AbstractFunctionTest;

public class ReduceTest extends AbstractFunctionTest {
   @Override
   protected Reduce getFunction() {
      return new Reduce(integerType());
   }

   @Override
   public void testEvaluate() {
      evaluate("(reduce + 0 [2 12 8])").to(22);
      evaluate("(reduce + 5 [2 12 8])").to(27);
      evaluate("(reduce * 1 [2 12 8])").to(192);
   }

   @Override
   public void testCanSimplify() {
      simplify("(reduce + 9 [2 12 8])").to("31");
   }

   @Override
   public void testCannotSimplify() {
   }
}
