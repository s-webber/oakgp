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

import java.util.Arrays;

import org.oakgp.function.AbstractFunctionTest;

public class ValuesTest extends AbstractFunctionTest {
   @Override
   protected Values getFunction() {
      return new Values();
   }

   @Override
   public void testEvaluate() {
      evaluate("(values {\"a\" 1})").to(Arrays.asList(1));
      evaluate("(values {\"a\" 7 \"b\" 180 \"c\" 42})").to(Arrays.asList(7, 180, 42));
   }

   @Override
   public void testCanSimplify() {
      simplify("(values {\"a\" 1})").to("[1]");
      simplify("(values {\"a\" 7 \"b\" 180 \"c\" 42})").to("[7 180 42]");
   }

   @Override
   public void testCannotSimplify() {
   }
}
