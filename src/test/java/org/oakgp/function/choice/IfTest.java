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
package org.oakgp.function.choice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.integerType;

import org.junit.Test;
import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Signature;

public class IfTest extends AbstractFunctionTest {
   private static final If IF = new If();

   @Override
   protected If getFunction() {
      return IF;
   }

   @Override
   public void testEvaluate() {
      evaluate("(if (> 9 8) (+ 1 2) (* 6 3))").to(3);
      evaluate("(if (> 8 9) (+ 1 2) (* 6 3))").to(18);
   }

   @Override
   public void testCanSimplify() {
      simplify("(if (> 2 1) 4 7)").to("4");
      simplify("(if (> 1 2) 4 7)").to("7");
      simplify("(if (> v0 v1) v2 v2)").to("v2");
      simplify("(if (> v0 9) (> v0 v1) (> v0 v1))").to("(> v0 v1)");

      simplify("(if (> v0 v1) (if (> v0 v1) v2 v3) v4)").to("(if (> v0 v1) v2 v4)");
      simplify("(if (> v0 v1) v2 (if (> v0 v1) v3 v4))").to("(if (> v0 v1) v2 v4)");

      simplify("(if (> v0 v1) (+ v2 (if (> v0 v1) v3 v4)) v5)").to("(if (> v0 v1) (+ v2 v3) v5)");
      simplify("(if (> v0 v1) v5 (+ v2 (if (> v0 v1) v3 v4)))").to("(if (> v0 v1) v5 (+ v2 v4))");

      simplify("(if (> v0 v1) (if (> v2 v3) (if (> v0 v1) v4 v5) v6) v7)").to("(if (> v0 v1) (if (> v2 v3) v4 v6) v7)");
      simplify("(if (> v0 v1) v7 (if (> v2 v3) (if (> v0 v1) v4 v5) v6))").to("(if (> v0 v1) v7 (if (> v2 v3) v5 v6))");

      simplify("(if (> v0 v1) true false)").to("(> v0 v1)");
      simplify("(if (> v0 v1) false true)").to("(>= v1 v0)");

      simplify("(if (>= v0 v1) (> v0 v1) (> v1 v0))").to("(if (>= v0 v1) (> v0 v1) true)");
      simplify("(if (>= v0 v1) (> v0 9) (> v0 v1))").to("(if (>= v0 v1) (> v0 9) false)");

      simplify("(if (= v0 v1) (>= v0 v1) (> v1 v0))").to("(if (= v0 v1) true (> v1 v0))");
      simplify("(if (> v0 v1) (>= v0 v1) (> v1 v0))").to("(if (> v0 v1) true (> v1 v0))");
      simplify("(if (> v0 v1) (>= v1 v0) (>= v0 v1))").to("(if (> v0 v1) false (>= v0 v1))");

      simplify("(if (> v0 v1) (>= v0 v1) (>= v1 v0))").to("true");
      simplify("(if (>= v0 v1) (> v1 v0) (= v1 v0))").to("false");
      simplify("(if (!= v0 v1) (= v0 v1) (> v1 v0))").to("false");

      simplify("(if (!= v0 v1) (= v3 v2) (and (= v0 v1) (> v3 v2)))").to("(if (!= v0 v1) (= v2 v3) (> v3 v2))");
      simplify("(if (!= v0 v1) (and (!= v0 v1) (> v3 v2)) (= v2 v3))").to("(if (!= v0 v1) (> v3 v2) (= v2 v3))");
      simplify("(if (!= v0 v1) (and (!= v0 v1) (> v3 v2)) (and (= v0 v1) (> v3 v2)))").to("(> v3 v2)");
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(if (> v0 v1) 1 2)", integerType(), integerType());
      cannotSimplify("(if (> v0 v1) v2 v3)", integerType(), integerType(), integerType(), integerType());
   }

   @Test
   public void testGetSignature() {
      Signature signature = getFunction().getSignature().create(integerType());
      assertSame(integerType(), signature.getReturnType());
      assertEquals(3, signature.getArgumentTypesLength());
      assertSame(booleanType(), signature.getArgumentType(0));
      assertSame(integerType(), signature.getArgumentType(1));
      assertSame(integerType(), signature.getArgumentType(2));
   }
}
