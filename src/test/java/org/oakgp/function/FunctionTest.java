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
package org.oakgp.function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.integerType;

import org.junit.Test;
import org.oakgp.Arguments;

public class FunctionTest {
   @Test
   public void testSimplify() {
      Function o = new Function() {
         @Override
         public Signature getSignature() {
            throw new UnsupportedOperationException();
         }

         @Override
         public Object evaluate(Arguments arguments) {
            throw new UnsupportedOperationException();
         }
      };
      assertNull(o.simplify(null));
   }

   @Test
   public void testIsPure() {
      assertTrue(new DummyFunction().isPure());
   }

   @Test
   public void testGetDisplayName() {
      assertEquals("dummy-function", new DummyFunction().getDisplayName());
   }

   @Test
   public void testGetDisplayNameBooleanReturnType() {
      assertEquals("boolean-dummy-function?", new IsBooleanDummyFunction().getDisplayName());
   }
}

class DummyFunction implements Function {
   @Override
   public Signature getSignature() {
      return Signature.createSignature(booleanType(), integerType());
   }

   @Override
   public Object evaluate(Arguments arguments) {
      throw new UnsupportedOperationException();
   }
}

class IsBooleanDummyFunction implements Function {
   @Override
   public Signature getSignature() {
      return Signature.createSignature(booleanType(), integerType());
   }

   @Override
   public Object evaluate(Arguments arguments) {
      throw new UnsupportedOperationException();
   }
}
