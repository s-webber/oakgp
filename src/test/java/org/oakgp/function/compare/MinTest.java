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

public class MinTest extends AbstractFunctionTest {
   @Override
   protected Min getFunction() {
      return Min.getSingleton();
   }

   @Override
   public void testEvaluate() {
      evaluate("(min 7 8)").to(7);
      evaluate("(min 8 8)").to(8);
      evaluate("(min 9 8)").to(8);

      evaluate("(min \"dog\" \"zebra\")").to("dog");
      evaluate("(min \"dog\" \"dog\")").to("dog");
      evaluate("(min \"dog\" \"apple\")").to("apple");
   }

   @Override
   public void testCanSimplify() {
      simplify("(min 7 8)").to("7");

      simplify("(min v0 v0)").to("v0");
      simplify("(min (max v0 1) (max v0 1))").to("(max v0 1)");
      simplify("(min 1 v0)").to("(min v0 1)");
      simplify("(min v0 (min 1 (min 7 (min 1 v0))))").to("(min v0 1)");
      simplify("(min (min v0 1) (min 7 (min 1 v0))))").to("(min v0 1)");
      simplify("(min (min v0 (min 1 7)) (min 1 v0))").to("(min v0 1)");
      simplify("(min (min v0 v1) (min v2 (min v3 v4)))").to("(min v4 (min v3 (min v2 (min v1 v0))))");

      simplify("(min (max v0 1) v0)").to("v0");
      simplify("(min (max v1 (max v0 1)) v0)").to("v0");
      simplify("(min (max v1 (max v0 1)) 1)").to("1");
      simplify("(min (max v2 (max v1 (max v0 1))) v0)").to("v0");
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(min v0 1)", integerType());
      cannotSimplify("(min v1 v0)", integerType(), integerType());
      cannotSimplify("(min v2 (min v1 v0))", integerType(), integerType(), integerType());
      cannotSimplify("(min v2 (min v1 (min v0 1)))", integerType(), integerType(), integerType());

      cannotSimplify("(min (max v0 1) (max v0 2))", integerType(), integerType());
      cannotSimplify("(min (max v0 1) (max v1 1))", integerType(), integerType());
      cannotSimplify("(min (max v2 v1) (max v0 1))", integerType(), integerType(), integerType());
      cannotSimplify("(min (max v2 v1) (max v3 (max v0 1)))", integerType(), integerType(), integerType(), integerType());
   }
}
