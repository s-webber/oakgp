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
package org.oakgp.function.coll;

import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.stringType;

import java.util.Arrays;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.util.FunctionSetBuilder;

public class KeysTest extends AbstractFunctionTest {
   @Override
   protected Keys getFunction() {
      return new Keys();
   }

   @Override
   public void testEvaluate() {
      evaluate("(keys {\"a\" 1})").to(Arrays.asList("a"));
      evaluate("(keys {\"a\" 7 \"b\" 180 \"c\" 42})").to(Arrays.asList("a", "b", "c"));
   }

   @Override
   public void testCanSimplify() {
      simplify("(keys {\"a\" 1})").to("[\"a\"]");
      simplify("(keys {\"a\" 7 \"b\" 180 \"c\" 42})").to("[\"a\" \"b\" \"c\"]");
   }

   @Override
   public void testCannotSimplify() {
   }

   @Override
   protected FunctionSet getFunctionSet() {
      return new FunctionSetBuilder().add(getFunction(), stringType(), integerType()).build();
   }
}
