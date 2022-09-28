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
package org.oakgp.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.TestUtils.stringConstant;
import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.nullableType;
import static org.oakgp.type.CommonTypes.stringType;

import org.junit.Test;

public class ConstantNodeTest {
   @Test
   public void testGetters() {
      final ConstantNode n = integerConstant(7);
      assertEquals(1, n.getNodeCount());
      assertEquals(1, n.getHeight());
      assertSame(integerType(), n.getType());
   }

   @Test
   public void testToString() {
      assertEquals("5", integerConstant(5).toString());
   }

   @Test
   public void testEvaluate() {
      Integer expected = 9;
      ConstantNode n = integerConstant(expected);
      Object actual = n.evaluate(null, null);
      assertSame(expected, actual);
   }

   @Test
   public void testEqualsAndHashCodeIntegers() {
      final ConstantNode n1 = integerConstant(7);
      final ConstantNode n2 = integerConstant(7);
      assertNotSame(n1, n2);
      assertEquals(n1, n1);
      assertEquals(n1.hashCode(), n2.hashCode());
      assertEquals(n1, n2);
   }

   @Test
   public void testEqualsAndHashCodeStrings() {
      String a = new String("hello");
      String b = new String("hello");
      assertNotSame(a, b);

      final ConstantNode n1 = stringConstant(a);
      final ConstantNode n2 = stringConstant(b);
      assertNotSame(n1, n2);
      assertEquals(n1, n1);
      assertEquals(n1.hashCode(), n2.hashCode());
      assertEquals(n1, n2);
   }

   @Test
   public void testNotEquals() {
      final ConstantNode n = integerConstant(7);
      assertNotEquals(n, integerConstant(8));
      assertNotEquals(n, integerConstant(-7));
      assertNotEquals(n, createVariable(7));
      assertNotEquals(n, stringConstant("7"));
      assertNotEquals(n, Integer.valueOf(7));
   }

   @Test
   public void testNull() {
      final ConstantNode nullConstant = new ConstantNode(null, nullableType(stringType()));

      assertEquals("null", nullConstant.toString());
      assertNull(nullConstant.evaluate(null, null));
      assertTrue(nullConstant.equals(nullConstant));
      assertTrue(nullConstant.equals(new ConstantNode(null, nullableType(stringType()))));
      assertFalse(nullConstant.equals(new ConstantNode(null, nullableType(integerType()))));

      final ConstantNode nonNullConstant = integerConstant(7);
      assertFalse(nullConstant.equals(nonNullConstant));
      assertFalse(nonNullConstant.equals(nullConstant));
   }
}
