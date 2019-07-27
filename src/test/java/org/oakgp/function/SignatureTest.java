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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.oakgp.TestUtils.assertUnmodifiable;
import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.functionType;
import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.listType;

import java.util.List;

import org.junit.Test;
import org.oakgp.TestUtils;
import org.oakgp.type.TypeBuilder;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;

public class SignatureTest {
   @Test
   public void testGetArgumentType() {
      Signature signature = Signature.createSignature(integerType(), booleanType(), integerType(), booleanType());

      assertEquals(3, signature.getArgumentTypesLength());
      assertSame(integerType(), signature.getReturnType());
      assertSame(booleanType(), signature.getArgumentType(0));
      assertSame(integerType(), signature.getArgumentType(1));
      assertSame(booleanType(), signature.getArgumentType(2));

      assertArrayIndexOutOfBoundsException(signature, -1);
      assertArrayIndexOutOfBoundsException(signature, 3);
   }

   private void assertArrayIndexOutOfBoundsException(Signature signature, int index) {
      try {
         signature.getArgumentType(index);
         fail();
      } catch (ArrayIndexOutOfBoundsException e) {
         // expected
      }
   }

   @Test
   public void testGetArgumentTypes() {
      Signature signature = Signature.createSignature(integerType(), booleanType(), integerType(), booleanType());
      List<Type> types = signature.getArgumentTypes();
      assertEquals(3, types.size());
      assertSame(booleanType(), types.get(0));
      assertSame(integerType(), types.get(1));
      assertSame(booleanType(), types.get(2));
      assertUnmodifiable(types);
   }

   @Test
   public void testToString() {
      Signature signature = Signature.createSignature(integerType(), booleanType(), integerType(), booleanType());
      assertEquals("Integer [Boolean, Integer, Boolean]", signature.toString());
   }

   @Test
   public void testEquals() {
      Signature s1 = Signature.createSignature(integerType(), booleanType(), integerType(), booleanType());
      Signature s2 = Signature.createSignature(integerType(), booleanType(), integerType(), booleanType());
      assertEquals(s1.hashCode(), s2.hashCode());
      assertTrue(s1.equals(s1));
      assertTrue(s1.equals(s2));
      assertTrue(s2.equals(s1));
   }

   @Test
   public void testNotEquals() {
      Signature s1 = Signature.createSignature(integerType(), booleanType(), integerType(), booleanType());
      Signature s2 = Signature.createSignature(integerType(), integerType(), booleanType(), booleanType());
      Signature s3 = Signature.createSignature(booleanType(), booleanType(), integerType(), booleanType());
      Signature s4 = Signature.createSignature(integerType(), booleanType(), integerType(), booleanType(), integerType());
      Signature s5 = Signature.createSignature(integerType(), booleanType(), integerType(), booleanType(), booleanType());

      assertTrue(s1.equals(s1));
      assertFalse(s1.equals(s2));
      assertFalse(s1.equals(s3));
      assertFalse(s1.equals(s4));
      assertFalse(s1.equals(s5));

      assertTrue(s2.equals(s2));
      assertFalse(s2.equals(s1));
      assertFalse(s2.equals(s3));
      assertFalse(s2.equals(s4));
      assertFalse(s2.equals(s5));

      assertTrue(s3.equals(s3));
      assertFalse(s3.equals(s1));
      assertFalse(s3.equals(s2));
      assertFalse(s3.equals(s4));
      assertFalse(s3.equals(s5));

      assertTrue(s4.equals(s4));
      assertFalse(s4.equals(s1));
      assertFalse(s4.equals(s2));
      assertFalse(s4.equals(s3));
      assertFalse(s4.equals(s5));

      assertTrue(s5.equals(s5));
      assertFalse(s5.equals(s1));
      assertFalse(s5.equals(s2));
      assertFalse(s5.equals(s3));
      assertFalse(s5.equals(s4));

      assertFalse(s1.equals("string"));
   }

   @Test
   public void testIsTemplate() {
      Type generic = Types.generic("X");
      assertTrue(Signature.createSignature(generic).isTemplate());
      assertTrue(Signature.createSignature(listType(generic)).isTemplate());
      assertTrue(Signature.createSignature(listType(listType(generic))).isTemplate());
      assertTrue(Signature.createSignature(integerType(), generic).isTemplate());
      assertTrue(Signature.createSignature(generic, booleanType()).isTemplate());
      assertTrue(Signature.createSignature(generic, generic).isTemplate());

      assertFalse(Signature.createSignature(integerType()).isTemplate());
      assertFalse(Signature.createSignature(integerType(), booleanType()).isTemplate());
   }

   @Test
   public void testCreate_singleGeneric() {
      Type superType = TestUtils.uniqueType();
      Type generic = Types.generic("X", superType);
      Signature template = Signature.createSignature(generic, generic, listType(generic));
      assertTrue(template.isTemplate());

      Type subType = TypeBuilder.name(TestUtils.uniqueTypeName()).parents(superType).build();
      Signature output = template.create(subType);
      assertSame(subType, output.getReturnType());
      assertEquals(2, output.getArgumentTypesLength());
      assertSame(subType, output.getArgumentType(0));
      assertSame(listType(subType), output.getArgumentType(1));
   }

   @Test
   public void testCreate_multipleGenerics() {
      Type from = Types.generic("X");
      Type to = Types.generic("Y");
      Signature template = Signature.createSignature(listType(to), functionType(to, from), listType(from));
      Signature output = template.create(integerType(), booleanType());
      assertTrue(template.isTemplate());
      assertFalse(output.isTemplate());
      assertSame(listType(to), template.getReturnType());
      assertSame(listType(integerType()), output.getReturnType());
   }

   @Test
   public void testCreate_invalidType() {
      Type superType = TestUtils.uniqueType();
      Type generic = Types.generic("X", superType);
      Signature template = Signature.createSignature(generic, generic, listType(generic));
      assertTrue(template.isTemplate());

      try {
         template.create(booleanType());
      } catch (IllegalArgumentException e) {
         assertEquals("Boolean not of types [" + superType.getName() + "]", e.getMessage());
      }
   }

   @Test
   public void testCreate_invalidNumberOfArguments() {
      Type generic1 = Types.generic("X");
      Type generic2 = Types.generic("Y");
      Signature template = Signature.createSignature(generic1, integerType(), generic2);
      assertTrue(template.isTemplate());

      try {
         template.create(booleanType());
      } catch (IllegalArgumentException e) {
         assertEquals("Wrong number of arguments 1 != 2", e.getMessage());
      }

      try {
         template.create(booleanType(), booleanType(), booleanType());
      } catch (IllegalArgumentException e) {
         assertEquals("Wrong number of arguments 3 != 2", e.getMessage());
      }
   }
}
