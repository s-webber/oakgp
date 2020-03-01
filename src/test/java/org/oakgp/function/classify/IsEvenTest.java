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

import static org.oakgp.type.CommonTypes.integerType;

import org.oakgp.function.AbstractFunctionTest;

public class IsEvenTest extends AbstractFunctionTest {
   @Override
   protected IsEven getFunction() {
      return IsEven.getSingleton();
   }

   @Override
   public void testEvaluate() {
      evaluate("(even? 28)").to(true);
      evaluate("(even? 27)").to(false);
      evaluate("(even? 2)").to(true);
      evaluate("(even? 1)").to(false);
      evaluate("(even? 0)").to(true);
      evaluate("(even? -1)").to(false);
      evaluate("(even? -2)").to(true);
      evaluate("(even? -27)").to(false);
      evaluate("(even? -28)").to(true);
   }

   @Override
   public void testCanSimplify() {
      simplify("(even? 1)").to("false");
      simplify("(even? 2)").to("true");
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(even? v0)", integerType());
   }

   @Override
   protected BooleanFunctionExpectationsBuilder createBooleanFunctionExpectationsBuilder() {
      return new BooleanFunctionExpectationsBuilder("(even? v0)") //
            .opposite("(odd? v0)");
   }
}
