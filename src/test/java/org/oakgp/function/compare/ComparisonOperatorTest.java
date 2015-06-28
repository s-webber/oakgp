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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.oakgp.Type.booleanType;

import org.junit.Test;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.function.Function;

public class ComparisonOperatorTest {
   @Test
   public void testGetSignature() {
      Type type = Type.type("ComparisonOperatorTest");
      Function f = new ComparisonOperator(type, true) {
         @Override
         protected boolean evaluate(int diff) {
            throw new UnsupportedOperationException();
         }
      };
      Signature signature = f.getSignature();
      assertSame(booleanType(), signature.getReturnType());
      assertEquals(2, signature.getArgumentTypesLength());
      assertSame(type, signature.getArgumentType(0));
      assertSame(type, signature.getArgumentType(1));
   }
}
