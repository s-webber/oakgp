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
package org.oakgp.primitive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.oakgp.TestUtils.assertUnmodifiable;
import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.stringType;

import java.util.List;

import org.junit.Test;
import org.oakgp.node.VariableNode;

public class VariableSetTest {
   @Test
   public void testGetById() {
      VariableSet s = VariableSet.createVariableSet(integerType(), booleanType(), integerType());

      VariableNode v0 = s.getById(0);
      VariableNode v1 = s.getById(1);
      VariableNode v2 = s.getById(2);

      assertSame(v0, s.getById(0));
      assertSame(integerType(), v0.getType());

      assertSame(v1, s.getById(1));
      assertSame(booleanType(), v1.getType());

      assertSame(v2, s.getById(2));
      assertSame(integerType(), v2.getType());

      assertNotEquals(v0, v1);
      assertNotEquals(v0, v2);
      assertNotEquals(v1, v2);
   }

   @Test
   public void assertGetByType() {
      VariableSet s = VariableSet.createVariableSet(integerType(), booleanType(), integerType());

      VariableNode v0 = s.getById(0);
      VariableNode v1 = s.getById(1);
      VariableNode v2 = s.getById(2);

      List<VariableNode> integers = s.getByType(integerType());
      assertEquals(2, integers.size());
      assertSame(v0, integers.get(0));
      assertSame(v2, integers.get(1));

      List<VariableNode> booleans = s.getByType(booleanType());
      assertEquals(1, booleans.size());
      assertSame(v1, booleans.get(0));

      assertNull(s.getByType(stringType()));
   }

   @Test
   public void assertGetByTypeUnmodifiable() {
      VariableSet s = VariableSet.createVariableSet(integerType(), booleanType(), integerType());
      List<VariableNode> integers = s.getByType(integerType());
      assertUnmodifiable(integers);
   }

   @Test
   public void assertSize() {
      assertEquals(0, VariableSet.createVariableSet().size());
      assertEquals(1, VariableSet.createVariableSet(integerType()).size());
      assertEquals(2, VariableSet.createVariableSet(integerType(), integerType()).size());
      assertEquals(2, VariableSet.createVariableSet(integerType(), booleanType()).size());
      assertEquals(3, VariableSet.createVariableSet(integerType(), booleanType(), integerType()).size());
   }
}
