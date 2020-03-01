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

public class IsPositiveTest extends AbstractFunctionTest {
   @Override
   protected IsPositive getFunction() {
      return IsPositive.getSingleton();
   }

   @Override
   public void testEvaluate() {
      evaluate("(pos? 27)").to(true);
      evaluate("(pos? 1)").to(true);
      evaluate("(pos? 0)").to(false);
      evaluate("(pos? -1)").to(false);
      evaluate("(pos? -27)").to(false);
   }

   @Override
   public void testCanSimplify() {
      simplify("(pos? 1)").to("true");
      simplify("(pos? -1)").to("false");
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(pos? v0)", integerType());
   }

   @Override
   protected BooleanFunctionExpectationsBuilder createBooleanFunctionExpectationsBuilder() {
      return new BooleanFunctionExpectationsBuilder("(pos? v0)") //
            .incompatibles("(neg? v0)", "(zero? v0)");
   }
}
