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
package org.oakgp.function.math;

import static org.junit.Assert.assertEquals;
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.TestUtils.longConstant;

import org.junit.Test;
import org.oakgp.Assignments;

public class LongUtilsTest {
   private static final NumberUtils<Long> UTILS = LongUtils.LONG_UTILS;

   @Test
   public void testAddPrimitive() {
      assertEquals(Long.valueOf(9), UTILS.add(7L, 2L));
   }

   @Test
   public void testAddNode() {
      assertEquals(Long.valueOf(9), UTILS.add(longConstant(7), createVariable(0), Assignments.createAssignments(2L)));
   }

   @Test
   public void testSubtractPrimitive() {
      assertEquals(Long.valueOf(5), UTILS.subtract(7L, 2L));
   }

   @Test
   public void testSubtractNode() {
      assertEquals(Long.valueOf(5), UTILS.subtract(longConstant(7), createVariable(0), Assignments.createAssignments(2L)));
   }

   @Test
   public void testMultiplyPrimitive() {
      assertEquals(Long.valueOf(14), UTILS.multiply(7L, 2L));
   }

   @Test
   public void testMultiplyNode() {
      assertEquals(Long.valueOf(14), UTILS.multiply(longConstant(7), createVariable(0), Assignments.createAssignments(2L)));
   }

   @Test
   public void testDividePrimitive() {
      assertEquals(Long.valueOf(3), UTILS.divide(7L, 2L));
   }

   @Test
   public void testDivideNode() {
      assertEquals(Long.valueOf(3), UTILS.divide(longConstant(7), createVariable(0), Assignments.createAssignments(2L)));
   }
}
