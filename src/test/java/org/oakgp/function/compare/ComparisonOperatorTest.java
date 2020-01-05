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
import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.comparableType;

import org.junit.Test;
import org.oakgp.function.Function;
import org.oakgp.function.RulesEngine;
import org.oakgp.function.Signature;
import org.oakgp.node.FunctionNode;
import org.oakgp.type.TypeBuilder;
import org.oakgp.type.Types.Type;

public class ComparisonOperatorTest {
   @Test
   public void testGetSignature() {
      Type type = TypeBuilder.name("ComparisonOperatorTest").parents(comparableType()).build();
      Function f = new ComparisonOperator(true) {
         @Override
         protected boolean evaluate(int diff) {
            throw new UnsupportedOperationException();
         }

         @Override
         public RulesEngine getEngine(FunctionNode fn) {
            throw new UnsupportedOperationException();
         }
      };
      Signature signature = f.getSignature().create(type);
      assertSame(booleanType(), signature.getReturnType());
      assertEquals(2, signature.getArgumentTypesLength());
      assertSame(type, signature.getArgumentType(0));
      assertSame(type, signature.getArgumentType(1));
   }
}
