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
import static org.oakgp.type.CommonTypes.integerType;

import org.junit.Test;
import org.oakgp.node.ConstantNode;

public class IntegerUtilsTest { // TODO add more tests of IntegerUtils and other NumberUtils sub-classes
   private static final NumberUtils<Integer> UTILS = IntegerUtils.INTEGER_UTILS;

   @Test
   public void testType() {
      assertEquals(integerType(), UTILS.getType());
   }

   @Test
   public void testRawZero() {
      assertEquals(Integer.valueOf(0), UTILS.rawZero());
   }

   @Test
   public void testZero() {
      ConstantNode zero = UTILS.zero();
      assertEquals(Integer.valueOf(0), zero.evaluate(null, null));
      assertEquals(integerType(), zero.getType());
   }

   @Test
   public void testAddPrimitive() {
      assertEquals(Integer.valueOf(9), UTILS.add(7, 2));
   }

   @Test
   public void testSubtractPrimitive() {
      assertEquals(Integer.valueOf(5), UTILS.subtract(7, 2));
   }

   @Test
   public void testMultiplyPrimitive() {
      assertEquals(Integer.valueOf(14), UTILS.multiply(7, 2));
   }

   @Test
   public void testDividePrimitive() {
      assertEquals(Integer.valueOf(3), UTILS.divide(7, 2));
   }
}
