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
package org.oakgp.function.compare;

import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.stringType;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.util.FunctionSetBuilder;

public class MaxTest extends AbstractFunctionTest {
   @Override
   protected Max getFunction() {
      return new Max();
   }

   @Override
   public void testEvaluate() {
      evaluate("(max 7 8)").to(8);
      evaluate("(max 8 8)").to(8);
      evaluate("(max 9 8)").to(9);

      evaluate("(max \"dog\" \"zebra\")").to("zebra");
      evaluate("(max \"dog\" \"dog\")").to("dog");
      evaluate("(max \"dog\" \"apple\")").to("dog");
   }

   @Override
   public void testCanSimplify() {
      simplify("(max v0 v0)").to("v0");

      // TODO (max v1 v0) -> (max v0 v1)
      // TODO (max v0 (max 9 v0)) -> (max v0 9)
      // TODO (max v0 (max v1 v2)) -> (max [v0 v1 v2])
   }

   @Override
   public void testCannotSimplify() {
   }

   @Override
   protected FunctionSet getFunctionSet() {
      Max function = getFunction();
      return new FunctionSetBuilder().add(function, integerType()).add(function, stringType()).build();
   }
}
