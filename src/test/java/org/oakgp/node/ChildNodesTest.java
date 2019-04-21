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
package org.oakgp.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.TestUtils.integerConstant;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ChildNodesTest {
   @Test
   public void testCreateChildNodes() {
      Node x = integerConstant(1);
      Node y = integerConstant(2);
      Node z = integerConstant(3);
      Node[] args = { x, y, z };
      ChildNodes first = ChildNodes.createChildNodes(args);
      assertChildNodes(first, x, y, z);

      Node a = integerConstant(4);
      args[1] = a;
      ChildNodes second = ChildNodes.createChildNodes(args);
      assertChildNodes(second, x, a, z);

      // assert the ChildNodes created first remains unchanged by subsequent changes to args
      assertChildNodes(first, x, y, z);
   }

   @Test
   public void testCreateChildNodesFromList() {
      Node x = integerConstant(1);
      Node y = integerConstant(2);
      Node z = integerConstant(3);
      Node[] array = { x, y, z };
      List<Node> list = Arrays.asList(array);
      ChildNodes first = ChildNodes.createChildNodes(array);
      ChildNodes second = ChildNodes.createChildNodes(list);
      assertEquals(first, second);
   }

   @Test
   public void testReplaceAt() {
      // create arguments
      Node x = integerConstant(1);
      Node y = integerConstant(2);
      Node z = integerConstant(3);
      ChildNodes original = ChildNodes.createChildNodes(x, y, z);
      assertChildNodes(original, x, y, z);

      // create new arguments based on original
      Node replacement = integerConstant(9);
      assertChildNodes(original.replaceAt(0, replacement), replacement, y, z);
      assertChildNodes(original.replaceAt(1, replacement), x, replacement, z);
      assertChildNodes(original.replaceAt(2, replacement), x, y, replacement);

      // assert original arguments has remained unchanged
      assertChildNodes(original, x, y, z);
   }

   @Test
   public void testSwap() {
      // create arguments
      Node x = integerConstant(1);
      Node y = integerConstant(2);
      Node z = integerConstant(3);
      ChildNodes original = ChildNodes.createChildNodes(x, y, z);

      assertChildNodes(original.swap(0, 0), x, y, z);
      assertChildNodes(original.swap(1, 1), x, y, z);
      assertChildNodes(original.swap(2, 2), x, y, z);

      assertChildNodes(original.swap(0, 1), y, x, z);
      assertChildNodes(original.swap(1, 0), y, x, z);

      assertChildNodes(original.swap(0, 2), z, y, x);
      assertChildNodes(original.swap(2, 0), z, y, x);

      assertChildNodes(original.swap(1, 2), x, z, y);
      assertChildNodes(original.swap(2, 1), x, z, y);
   }

   @Test
   public void testArrayIndexOutOfBoundsException() {
      ChildNodes arguments = ChildNodes.createChildNodes(integerConstant(7), integerConstant(42));
      assertArrayIndexOutOfBoundsException(arguments, -1);
      assertArrayIndexOutOfBoundsException(arguments, 2);
   }

   @Test
   public void testEqualsAndHashCode() {
      ChildNodes a1 = ChildNodes.createChildNodes(integerConstant(7), createVariable(0), integerConstant(42));
      ChildNodes a2 = ChildNodes.createChildNodes(integerConstant(7), createVariable(0), integerConstant(42));
      assertEquals(a1, a1);
      assertEquals(a1.hashCode(), a2.hashCode());
      assertEquals(a1, a2);
   }

   @Test
   public void testNotEquals() {
      ChildNodes a = ChildNodes.createChildNodes(integerConstant(7), createVariable(0), integerConstant(42));

      // same arguments, different order
      assertNotEquals(a, ChildNodes.createChildNodes(integerConstant(42), createVariable(0), integerConstant(7)));

      // different arguments
      assertNotEquals(a, ChildNodes.createChildNodes(integerConstant(7), createVariable(0), integerConstant(43)));

      // one fewer argument
      assertNotEquals(a, ChildNodes.createChildNodes(integerConstant(7), createVariable(0)));

      // one extra argument
      assertNotEquals(a, ChildNodes.createChildNodes(integerConstant(7), createVariable(0), integerConstant(42), integerConstant(42)));
   }

   @Test
   public void testToString() {
      ChildNodes arguments = ChildNodes.createChildNodes(integerConstant(7), createVariable(0), integerConstant(42));
      assertEquals("[7, v0, 42]", arguments.toString());
   }

   private void assertArrayIndexOutOfBoundsException(ChildNodes arguments, int index) {
      try {
         arguments.getNode(index);
         fail();
      } catch (ArrayIndexOutOfBoundsException e) {
         // expected
      }
   }

   /**
    * tests {@link ChildNodes#size()}, {@link ChildNodes#getNode(int)}, {@link ChildNodes#first()}, {@link ChildNodes#second()},
    * {@link ChildNodes#third()}
    */
   private void assertChildNodes(ChildNodes actual, Node... expected) {
      assertEquals(expected.length, actual.size());
      assertSame(expected[0], actual.first());
      assertSame(expected[1], actual.second());
      assertSame(expected[2], actual.third());
      for (int i = 0; i < expected.length; i++) {
         assertSame(expected[i], actual.getNode(i));
      }
   }
}
