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
package org.oakgp.function.compare;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.oakgp.Type.integerType;
import static org.oakgp.Type.stringType;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class NotEqualTest extends AbstractFunctionTest {
   @Override
   protected NotEqual getFunction() {
      return new NotEqual(integerType());
   }

   @Override
   public void testEvaluate() {
      evaluate("(!= 7 8)").to(TRUE);
      evaluate("(!= 8 8)").to(FALSE);
      evaluate("(!= 9 8)").to(TRUE);

      evaluate("(!= \"dog\" \"zebra\")").to(TRUE);
      evaluate("(!= \"dog\" \"dog\")").to(FALSE);
      evaluate("(!= \"dog\" \"apple\")").to(TRUE);
   }

   @Override
   public void testCanSimplify() {
      simplify("(!= v0 v0)").to("false");
      simplify("(!= 8 7)").to("true");
      simplify("(!= 8 8)").to("false");
      simplify("(!= 8 9)").to("true");
      simplify("(!= v0 8)").to("(!= 8 v0)");
      simplify("(!= v1 v0)").to("(!= v0 v1)");
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(!= 8 v0)", integerType());
      cannotSimplify("(!= v0 v1)", integerType(), integerType());
   }

   @Override
   protected Function[] getFunctionSet() {
      return new Function[] { getFunction(), new NotEqual(stringType()) };
   }
}
