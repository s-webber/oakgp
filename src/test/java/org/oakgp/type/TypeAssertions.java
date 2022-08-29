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

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;

import java.util.HashSet;
import java.util.Set;

import org.oakgp.type.Types.Type;

class TypeAssertions {
   static void assertName(Type t, String expected) {
      assertEquals(expected, t.getName());
   }

   static void assertNoParameters(Type t) {
      assertParameters(t);
   }

   static void assertParameters(Type t, Type... expected) {
      assertEquals(asList(expected), t.getParameters());
      for (int i = 0; i < expected.length; i++) {
         assertSame(expected[i], t.getParameter(i));
      }
      assertThrows(ArrayIndexOutOfBoundsException.class, () -> t.getParameter(expected.length));
   }

   static void assertNoParents(Type t) {
      assertParents(t);
   }

   static void assertParents(Type t, Type... expected) {
      Set<Type> expectedSet = new HashSet<>();
      expectedSet.addAll(asList(expected));
      expectedSet.add(Types.type("Object")); // Object is a parent of all other types
      assertEquals(expectedSet, t.getParents());
   }
}
