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
import org.oakgp.type.Types.Type;

public class OrTest extends AbstractFunctionTest {
   @Override
   protected Or getFunction() {
      return Or.getSingleton();
   }

   @Override
   public void testEvaluate() {
      evaluate("(or true true)").to(true);
      evaluate("(or true false)").to(true);
      evaluate("(or false true)").to(true);
      evaluate("(or false false)").to(false);

      evaluate("(or true (or true true))").to(true);
      evaluate("(or true (or true false))").to(true);
      evaluate("(or true (or false true))").to(true);
      evaluate("(or false (or true true))").to(true);
      evaluate("(or true (or false false))").to(true);
      evaluate("(or false (or false true))").to(true);
      evaluate("(or false (or true false))").to(true);
      evaluate("(or false (or false false))").to(false);
   }

   @Override
   public void testCanSimplify() {
      simplify("(or v0 v1)").with(booleanType(), booleanType()).to("(or v1 v0)");

      Type[] types = { booleanType(), booleanType(), booleanType(), booleanType() };

      simplify("(or true v0)").with(types).to("true");
      simplify("(or v0 true)").with(types).to("true");

      simplify("(or false v0)").with(types).to("v0");
      simplify("(or v0 false)").with(types).to("v0");

      simplify("(or v0 v0)").with(types).to("v0");

      simplify("(or (and (false? v0) v1) v0)").with(types).to("(or (and (false? v0) v1) v0)");
      simplify("(or (false? v0) (or (and v1 v0) (false? v1)))").with(types).to("true");

      simplify("(or v0 (or v1 (or v2 v3)))").with(types).to("(or v3 (or v2 (or v1 v0)))");
      simplify("(or v3 (or v2 (or v1 v0)))").with(types).to("(or v3 (or v2 (or v1 v0)))");
      simplify("(or v1 (or v0 (or v3 v2)))").with(types).to("(or v3 (or v2 (or v1 v0)))");
      simplify("(or (or v1 v0) (or v3 v2)))").with(types).to("(or v3 (or v2 (or v1 v0)))");

      simplify("(or (and v0 v1) v0)").with(types).to("v0");
      simplify("(or v0 (and v0 v1))").with(types).to("v0");
      // TODO simplify("(or (and v0 v1) (and v0 v2))").with(types).to("(and (or v2 v1) v0)");

      simplify("(or (= v0 v1) (!= v0 v1))").with(types).to("true)");
      simplify("(or (!= v0 v1) (= v0 v1))").with(types).to("true)");
      simplify("(or v0 (false? v0))").with(types).to("true");
      simplify("(or (false? v0) v0)").with(types).to("true");
      simplify("(or (>= v0 v1) (> v1 v0))").with(types).to("true");
      simplify("(or (> v1 v0) (>= v0 v1))").with(types).to("true");

      simplify("(or (> v1 v0) (!= v0 v1))").with(types).to("(!= v0 v1)");
      simplify("(or (!= v0 v1) (> v1 v0))").with(types).to("(!= v0 v1)");
      simplify("(or (>= v1 v0) (> v1 v0))").with(types).to("(>= v1 v0)");

      simplify("(or (or (> v1 v0) (>= v2 v3)) (or (> v3 v2) (>= v0 v1)))").with(types).to("true");
      simplify("(or (false? (and (even? v0) (odd? v0))) (even? v0))").with(integerType()).to("true)");

      simplify("(or (> v1 v0) (> v0 v1))").with(integerType(), integerType()).to("(!= v0 v1)");

      simplify("(or (pos? v0) (neg? v0))").with(integerType(), integerType()).to("(false? (zero? v0))");
      simplify("(or (neg? v0) (pos? v0))").with(integerType(), integerType()).to("(false? (zero? v0))");
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(or v1 v0)", booleanType(), booleanType());
      cannotSimplify("(or (and (> v1 v0) (!= v2 v3)) (and (> v2 v3) (!= v0 v1)))", booleanType(), booleanType(), booleanType(), booleanType());
      cannotSimplify("(or (and (> v1 v0) (>= v2 v3)) (and (> v3 v2) (>= v0 v1)))", booleanType(), booleanType(), booleanType(), booleanType());
   }
}
