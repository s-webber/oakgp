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

import static org.oakgp.type.CommonTypes.integerType;

import org.oakgp.function.AbstractFunctionTest;

public class IsZeroTest extends AbstractFunctionTest {
   @Override
   protected IsZero getFunction() {
      return IsZero.getSingleton();
   }

   @Override
   public void testEvaluate() {
      evaluate("(zero? 0)").to(true);
      evaluate("(zero? 1)").to(false);
      evaluate("(zero? 2)").to(false);
      evaluate("(zero? 27)").to(false);
      evaluate("(zero? -1)").to(false);
      evaluate("(zero? -2)").to(false);
      evaluate("(zero? -27)").to(false);
   }

   @Override
   public void testCanSimplify() {
      simplify("(zero? 0)").to("true");
      simplify("(zero? 1)").to("false");
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(zero? v0)", integerType());
   }

   @Override
   protected BooleanFunctionExpectationsBuilder createBooleanFunctionExpectationsBuilder() {
      return new BooleanFunctionExpectationsBuilder("(zero? v0)") //
            .incompatibles("(pos? v0)", "(neg? v0)", "(odd? v0)") //
            .consequences("(even? v0)");
   }
}
