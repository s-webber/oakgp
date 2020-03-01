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
package org.oakgp.function.bool;

import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.integerType;

import org.oakgp.function.AbstractFunctionTest;

public class XorTest extends AbstractFunctionTest {
   @Override
   protected Xor getFunction() {
      return Xor.getSingleton();
   }

   @Override
   public void testEvaluate() {
      evaluate("(xor true true)").to(false);
      evaluate("(xor true false)").to(true);
      evaluate("(xor false true)").to(true);
      evaluate("(xor false false)").to(false);

      evaluate("(xor true (xor true true))").to(true);
      evaluate("(xor true (xor true false))").to(false);
      evaluate("(xor true (xor false true))").to(false);
      evaluate("(xor false (xor true true))").to(false);
      evaluate("(xor true (xor false false))").to(true);
      evaluate("(xor false (xor false true))").to(true);
      evaluate("(xor false (xor true false))").to(true);
      evaluate("(xor false (xor false false))").to(false);
   }

   @Override
   public void testCanSimplify() {
      simplify("(xor v0 v1)").with(booleanType(), booleanType()).to("(xor v1 v0)");
      simplify("(xor true v0)").with(booleanType()).to("(false? v0)");
      simplify("(xor v0 true)").with(booleanType()).to("(false? v0)");

      simplify("(xor false v0)").with(booleanType()).to("v0");
      simplify("(xor v0 false)").with(booleanType()).to("v0");

      simplify("(xor v0 v0)").with(booleanType()).to("false");

      simplify("(xor v0 (false? v0))").with(booleanType()).to("true");
      simplify("(xor (= v0 v1) (or (> v0 v1) (> v1 v0)))").with(integerType(), integerType()).to("true");
      simplify("(xor (!= v0 v1) (or (> v1 v0) (> v0 v1)))").with(integerType(), integerType()).to("false");

      simplify("(xor (even? v0) (odd? v0))").with(integerType()).to("true");
   }

   @Override
   public void testCannotSimplify() {
      // TODO simplify "(or x y)"
      cannotSimplify("(xor v1 v0)", booleanType(), booleanType());

      // TODO why not "(xor (!= v3 v2) (= v1 v0))" - is ordering consistent between "xor", "or", "!=" and "="?
      cannotSimplify("(xor (!= v2 v3) (= v0 v1))", integerType(), integerType(), integerType(), integerType());

      // TODO simplify to "(xor (> v0 v1) (> v1 v0))"
      cannotSimplify("(xor (>= v1 v0) (>= v0 v1))", integerType(), integerType());

      // TODO simplify to "(!= v0 v1)"
      cannotSimplify("(xor (> v1 v0) (> v0 v1))", integerType(), integerType());

      // TODO simplify to "(= v0 v1)"
      cannotSimplify("(xor (>= v0 v1) (> v0 v1))", integerType(), integerType());

      // TODO simplify to "(false? (zero? v0))"
      cannotSimplify("(xor (neg? v0) (pos? v0))", integerType());
   }
}