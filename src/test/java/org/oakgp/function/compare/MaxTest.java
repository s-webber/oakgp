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

import org.oakgp.function.AbstractFunctionTest;

public class MaxTest extends AbstractFunctionTest {
   @Override
   protected Max getFunction() {
      return Max.getSingleton();
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
      simplify("(max (min v0 1) (min v0 1))").to("(min v0 1)");
      simplify("(max 1 v0)").to("(max v0 1)");
      simplify("(max v0 (max 1 (max 7 (max 1 v0))))").to("(max v0 7)");
      simplify("(max (max v0 1) (max 7 (max 1 v0))))").to("(max v0 7)");
      simplify("(max (max v0 (max 1 7)) (max 1 v0))").to("(max v0 7)");
      simplify("(max (max v0 v1) (max v2 (max v3 v4)))").to("(max v4 (max v3 (max v2 (max v1 v0))))");

      simplify("(max (min v0 1) v0)").to("v0");
      simplify("(max (min v1 (min v0 1)) v0)").to("v0");
      simplify("(max (min v1 (min v0 1)) 1)").to("1");
      simplify("(max (min v2 (min v1 (min v0 1))) v0)").to("v0");
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(max v0 1)", integerType());
      cannotSimplify("(max v1 v0)", integerType(), integerType());
      cannotSimplify("(max v2 (max v1 v0))", integerType(), integerType(), integerType());
      cannotSimplify("(max v2 (max v1 (max v0 1)))", integerType(), integerType(), integerType());

      cannotSimplify("(max (min v0 1) (min v0 2))", integerType(), integerType());
      cannotSimplify("(max (min v0 1) (min v1 1))", integerType(), integerType());
      cannotSimplify("(max (min v2 v1) (min v0 1))", integerType(), integerType(), integerType());
      cannotSimplify("(max (min v3 (min v0 1)) (min v2 v1))", integerType(), integerType(), integerType(), integerType());
   }
}
