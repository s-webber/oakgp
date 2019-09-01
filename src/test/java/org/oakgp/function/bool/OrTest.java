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

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.type.Types.Type;
import org.oakgp.util.FunctionSetBuilder;

public class OrTest extends AbstractFunctionTest {
   @Override
   protected Or getFunction() {
      return new Or();
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
      simplify("(or true v0)").with(booleanType()).to("true");
      simplify("(or v0 true)").with(booleanType()).to("true");

      simplify("(or false v0)").with(booleanType()).to("v0");
      simplify("(or v0 false)").with(booleanType()).to("v0");

      simplify("(or v0 v0)").with(booleanType()).to("v0");

      Type[] types = { booleanType(), booleanType(), booleanType(), booleanType() };
      simplify("(or v0 (or v1 (or v2 v3)))").with(types).to("(or v3 (or v2 (or v1 v0)))");
      simplify("(or v3 (or v2 (or v1 v0)))").with(types).to("(or v3 (or v2 (or v1 v0)))");
      simplify("(or v1 (or v0 (or v3 v2)))").with(types).to("(or v3 (or v2 (or v1 v0)))");
      simplify("(or (or v1 v0) (or v3 v2)))").with(types).to("(or v3 (or v2 (or v1 v0)))");
   }

   @Override
   public void testCannotSimplify() {
   }

   @Override
   protected FunctionSet getFunctionSet() {
      return new FunctionSetBuilder().add(getFunction()).build();
   }
}
