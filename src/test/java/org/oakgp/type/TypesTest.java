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
package org.oakgp.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.oakgp.TestUtils.uniqueType;
import static org.oakgp.TestUtils.uniqueTypeName;
import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.stringType;
import static org.oakgp.type.TypeAssertions.assertName;
import static org.oakgp.type.TypeAssertions.assertNoParameters;
import static org.oakgp.type.TypeAssertions.assertNoParents;
import static org.oakgp.type.TypeAssertions.assertParameters;
import static org.oakgp.type.TypeAssertions.assertParents;
import static org.oakgp.type.TypeBuilder.name;

import java.util.Collections;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.oakgp.type.Types.Type;

public class TypesTest {
   @Test
   public void declareMultipleTimes() {
      String name = uniqueTypeName();
      Types.declareType(name);
      try {
         Types.declareType(name);
         fail();
      } catch (IllegalStateException e) {
         assertEquals("Type already declared: " + name, e.getMessage());
      }
      try {
         name(name).parameters(stringType()).build();
         fail();
      } catch (IllegalStateException e) {
         assertEquals("Type already declared: " + name, e.getMessage());
      }
      try {
         name(name).parents(stringType()).build();
         fail();
      } catch (IllegalStateException e) {
         assertEquals("Type already declared: " + name, e.getMessage());
      }
   }

   @Test
   public void no_parameters_no_parents() {
      String name = uniqueTypeName();
      Type type = Types.declareType(name);

      assertEquals(type, type);
      assertSame(type, Types.type(name));
      assertName(type, name);
      assertEquals(type.toString(), name);
      assertNoParameters(type);
      assertNoParents(type);

      assertParameterLengthMismatch(name, CommonTypes.integerType());
   }

   @Test
   public void multiple_parameters_no_parents() {
      String name = uniqueTypeName();
      Type parameter1 = CommonTypes.integerType();
      Type parameter2 = CommonTypes.stringType();
      Type type = name(name).parameters(parameter1, parameter2).build();

      assertEquals(type, type);
      assertSame(type, Types.type(name, parameter1, parameter2));
      assertName(type, name);
      assertEquals(type.toString(), name + " [Integer, String]");
      assertParameters(type, integerType(), stringType());
      assertNoParents(type);

      assertParameterMismatch(name, parameter2, parameter1);
      assertParameterMismatch(name, parameter1, parameter1);
      assertParameterMismatch(name, parameter2, parameter2);
      assertParameterLengthMismatch(name);
      assertParameterLengthMismatch(name, parameter1);
      assertParameterLengthMismatch(name, parameter1, parameter2, parameter2);
   }

   @Test
   public void single_inheritance_hierarchy() {
      Type a = uniqueType();
      Type b = name(uniqueTypeName()).parents(a).build();
      Type c = name(uniqueTypeName()).parents(b).build();
      Type d = name(uniqueTypeName()).parents(c).build();

      assertNoParents(a);
      assertParents(b, a);
      assertParents(c, b);
      assertParents(d, c);
   }

   @Test
   public void single_inheritance_hierarchy_with_generics() {
      Type x = Types.generic("X");
      Type y = Types.generic("Y");
      Type a = name(uniqueTypeName()).parameters(x, y).build();

      Type z = Types.generic("Z");
      Type b = name(uniqueTypeName()).parameters(z).parents(Types.type(a.getName(), z, z)).build();

      Type stringType = CommonTypes.stringType();
      Type c = name(uniqueTypeName()).parents(Types.type(b.getName(), stringType)).build();
      assertNoParents(a);
      assertParents(b, Types.type(a.getName(), z, z));
      assertParents(c, Types.type(b.getName(), stringType));

      assertSame(getSingleParent(c), Types.type(b.getName(), stringType));
      assertSame(getSingleParent(getSingleParent(c)), Types.type(a.getName(), stringType, stringType));
   }

   @Test
   public void multiple_subclasses_of_parent_with_generics() {
      Type x = Types.generic("X");
      Type a = name(uniqueTypeName()).parameters(x).build();

      Type b = name(uniqueTypeName()).parents(Types.type(a.getName(), CommonTypes.stringType())).build();
      Type c = name(uniqueTypeName()).parents(Types.type(a.getName(), CommonTypes.stringType())).build();

      assertNoParents(a);
      assertParents(b, Types.type(a.getName(), CommonTypes.stringType()));
      assertParents(c, Types.type(a.getName(), CommonTypes.stringType()));
      assertSame(getSingleParent(b), getSingleParent(c));
   }

   @Test
   public void parameter() { // TODO
      Type superType = CommonTypes.comparableType();
      Type subType = CommonTypes.stringType();

      String name = uniqueTypeName();
      Type type = name(name).parameters(superType).build();
      assertSame(type, Types.type(name, superType));
      assertParameterMismatch(name, subType);
   }

   @Test
   public void declareWithParametersThatHasParameter() {
      String name = uniqueTypeName();

      Type type = Types.declareType(name, new Type[0], new Type[] { CommonTypes.listType(CommonTypes.stringType()) });
      assertEquals(name, type.getName());
      assertEquals(name + " [List [String]]", type.toString());
      assertTrue(type.getParents().isEmpty());
      assertSame(type, Types.type(name, CommonTypes.listType(CommonTypes.stringType())));
      assertEquals(type, type);
      assertParameterMismatch(name, CommonTypes.listType(CommonTypes.integerType()));
   }

   @Test
   public void genericWithoutParents() {
      String name = uniqueTypeName();
      Type generic = Types.generic(name);

      assertEquals(generic, generic);
      assertName(generic, name);
      assertEquals(generic.toString(), name);
      assertNoParameters(generic);
      assertNoParents(generic);

      // Every generic is unique. They are not equal to anything but themselves, even other types with the same name.
      assertNotEquals(generic, Types.generic(name));
      assertNotEquals(generic, Types.declareType(name));
   }

   @Test
   public void genericWithParent() {
      String name = uniqueTypeName();

      // decalte type that extends
      Type superType = CommonTypes.comparableType();
      Type parameter = Types.generic("X", superType);
      Type template = Types.declareType(name, new Type[0], new Type[] { parameter });
      assertEquals(Collections.singleton(CommonTypes.comparableType()), parameter.getParents());

      // string and integer both extend comparable
      Type type1 = Types.type(name, CommonTypes.stringType());
      assertSame(type1, Types.type(name, CommonTypes.stringType()));
      assertEquals(name + " [String]", type1.toString());

      Type type2 = Types.type(name, CommonTypes.integerType());
      assertSame(type2, Types.type(name, CommonTypes.integerType()));
      assertEquals(name + " [Integer]", type2.toString());

      Type type3 = Types.type(name, superType);
      assertSame(type3, Types.type(name, superType));
      assertEquals(name + " [Comparable]", type3.toString());

      assertNotEquals(template, type1);
      assertNotEquals(template, type2);
      assertNotEquals(template, type3);
      assertNotEquals(type1, type2);
      assertNotEquals(type1, type3);
      assertNotEquals(type2, type3);

      try {
         Types.type(name, Types.declareType("object"));
         fail();
      } catch (RuntimeException e) {
         assertEquals("object not of types [Comparable]", e.getMessage());
      }
   }

   private void assertParameterLengthMismatch(String name, Type... parameters) {
      try {
         Types.type(name, parameters);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("Parameter length mismatch", e.getMessage());
      }
   }

   private void assertParameterMismatch(String name, Type... parameters) {
      try {
         Types.type(name, parameters);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("Parameter mismatch", e.getMessage());
      }
   }

   private Type getSingleParent(Type t) {
      Set<Type> parents = t.getParents();
      if (parents.size() == 1) {
         return parents.iterator().next();
      } else {
         throw new IllegalStateException(t + " " + parents);
      }
   }

   @Test
   public void test() {
      Type z = Types.generic("Z");
      Type y = Types.generic("Y");
      Type x = Types.generic("X");
      Type string = CommonTypes.stringType();
      Type xxx = Types.declareType("xxx", new Type[0], new Type[] { y, z });
      Type zzz = Types.declareType("zzz", new Type[] { Types.type("xxx", x, x) }, new Type[] { x });
      System.out.println("z4" + zzz + " " + zzz.getParents());
      Type listStrings = Types.type("zzz", string);
      System.out.println("z5" + listStrings + " " + listStrings.getParents());
      Type qqq = Types.type("xxx", CommonTypes.bigIntegerType(), CommonTypes.bigDecimalType());
      System.out.println("z6" + qqq + " " + qqq.getParents());
      Type zzz2 = Types.declareType("zzz2", new Type[] { Types.type("xxx", CommonTypes.bigIntegerType(), CommonTypes.bigDecimalType()) }, new Type[0]);
      System.out.println("z7" + zzz2 + " " + zzz2.getParents());

      Type p1 = CommonTypes.listType(x);
      Type p2 = CommonTypes.functionType(x, y);
      Type p3 = CommonTypes.listType(y);
      System.out.println("z733" + zzz2 + " " + zzz2.getParents());
      Type dsds = TypeBuilder.name("xxx2").parents(p1, p2, p3).parameters(x, y).build();
      Type dsds2 = Types.type("xxx2", string, CommonTypes.bigIntegerType());
      System.out.println("z8" + dsds + " " + dsds.getParents());
      System.out.println("z9" + dsds2 + " " + dsds2.getParents());
   }

   @Test
   public void test1() {
      Type type = Types.declareType(uniqueTypeName(), new Type[0], new Type[] { CommonTypes.stringType() });
      try {
         Types.type(type.getName(), Types.generic("X", CommonTypes.bigIntegerType()));
         Assert.fail();
      } catch (RuntimeException e) {

      }
   }
}
