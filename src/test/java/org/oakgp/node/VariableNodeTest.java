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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.oakgp.Assignments.createAssignments;
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.Type.integerType;

import org.junit.Test;
import org.oakgp.Assignments;
import org.oakgp.Type;

public class VariableNodeTest {
   @Test
   public void testGetters() {
      final int id = 7;
      final VariableNode v = createVariable(id);
      assertEquals(id, v.getId());
      assertEquals(1, v.getNodeCount());
      assertEquals(1, v.getHeight());
      assertSame(integerType(), v.getType());
   }

   @Test
   public void testToString() {
      assertEquals("v5", createVariable(5).toString());
   }

   @Test
   public void testEvaluate() {
      final Integer expected = 9;
      final VariableNode v = createVariable(0);
      final Assignments assignments = createAssignments(expected);
      final Object actual = (int) v.evaluate(assignments);
      assertSame(expected, actual);
   }

   @Test
   public void testEqualsAndHashCode() {
      final VariableNode n1 = new VariableNode(1, Type.integerType());
      final VariableNode n2 = new VariableNode(1, Type.integerType());
      assertNotSame(n1, n2);
      assertEquals(n1, n1);
      assertEquals(n2, n2);
      assertEquals(n1.hashCode(), n2.hashCode());
      // NOTE: *not* over-riding equals(Object) as two VariableNode references are only "equal" if they refer to the same instance
      assertNotEquals(n1, n2);
   }

   @Test
   public void testNotEquals() {
      final VariableNode n = createVariable(1);
      assertNotEquals(n, createVariable(0));
      assertNotEquals(n, createVariable(2));
      assertNotEquals(n, integerConstant(1));
      assertNotEquals(n, Integer.valueOf(1));
   }
}
