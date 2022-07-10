/*
 * Copyright 2022 S. Webber
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
package org.oakgp.function.pair;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.entryType;

import org.junit.Test;
import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;

public class CreatePairTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new CreatePair();
   }

   @Override
   public void testEvaluate() {
      evaluate("(create-pair 3 7)").to(new Pair(3, 7));
      evaluate("(create-pair \"a\" 9)").to(new Pair("a", 9));
   }

   @Override
   public void testCanSimplify() {
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(create-pair v0 v1)", booleanType(), integerType());
      cannotSimplify("(create-pair v0 v0)", booleanType(), booleanType());
   }

   @Test
   public void testGetSignature() {
      Signature signature = getFunction().getSignature().create(booleanType(), integerType());
      assertSame(entryType(booleanType(), integerType()), signature.getReturnType());
      assertEquals(2, signature.getArgumentTypesLength());
      assertSame(booleanType(), signature.getArgumentType(0));
      assertSame(integerType(), signature.getArgumentType(1));
   }
}
