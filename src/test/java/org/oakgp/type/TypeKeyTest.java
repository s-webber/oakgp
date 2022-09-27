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
package org.oakgp.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.stringType;

import org.junit.Test;
import org.oakgp.type.Types.Type;

public class TypeKeyTest {
   @Test
   public void sameNameAndParameters() {
      TypeKey key = new TypeKey("test", integerType(), stringType());
      assertEquals(key, key);
      assertEquals(key.hashCode(), key.hashCode());

      TypeKey copy = new TypeKey("test", integerType(), stringType());
      assertEquals(key, copy);
      assertEquals(key.hashCode(), copy.hashCode());
   }

   @Test
   public void sameNameDifferentParameters() {
      String name = "test";
      TypeKey key = new TypeKey("test", integerType(), stringType());

      assertNotEquals(key, new TypeKey(name));
      assertNotEquals(key, new TypeKey(name, integerType()));
      assertNotEquals(key, new TypeKey(name, stringType(), integerType()));
      assertNotEquals(key, new TypeKey(name, integerType(), stringType(), stringType()));
   }

   @Test
   public void sameParametersDifferentNames() {
      Type parameter = stringType();
      TypeKey key = new TypeKey("test", parameter);

      assertNotEquals(key, new TypeKey("TEST", parameter));
      assertNotEquals(key, new TypeKey("tes", parameter));
      assertNotEquals(key, new TypeKey("testx", parameter));
      assertNotEquals(key, new TypeKey(" test", parameter));
      assertNotEquals(key, new TypeKey("test ", parameter));
      assertNotEquals(key, new TypeKey("te_st", parameter));
      assertNotEquals(key, new TypeKey("etts", parameter));
      assertNotEquals(key, new TypeKey("wxyz", parameter));
   }

   @Test
   public void toStringWithParameters() {
      assertEquals("test1 [java.lang.Integer, java.lang.String]", new TypeKey("test1", integerType(), stringType()).toString());
   }

   @Test
   public void toStringWithoutParameters() {
      assertEquals("test2", new TypeKey("test2").toString());
   }
}
