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
import static org.oakgp.type.CommonTypes.entryType;
import static org.oakgp.type.CommonTypes.integerType;

import org.junit.Test;
import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Signature;

public class KeyTest extends AbstractFunctionTest {
   @Override
   protected Key getFunction() {
      return new Key();
   }

   @Override
   public void testEvaluate() {
      evaluate("(key (create-pair 3 7))").to(3);
   }

   @Override
   public void testCanSimplify() {
   }

   @Override
   public void testCannotSimplify() {
   }

   @Test
   public void testGetSignature() {
      Signature signature = getFunction().getSignature().create(booleanType(), integerType());
      assertSame(booleanType(), signature.getReturnType());
      assertEquals(1, signature.getArgumentTypesLength());
      assertSame(entryType(booleanType(), integerType()), signature.getArgumentType(0));
   }
}
