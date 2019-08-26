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

public class MinTest extends AbstractFunctionTest {
   @Override
   protected Min getFunction() {
      return new Min();
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
      simplify("(min v0 v0)").to("v0");
   }

   @Override
   public void testCannotSimplify() {
   }

   @Override
   protected FunctionSet getFunctionSet() {
      Min function = getFunction();
      return new FunctionSetBuilder().add(function, integerType()).add(function, stringType()).build();
   }
}
