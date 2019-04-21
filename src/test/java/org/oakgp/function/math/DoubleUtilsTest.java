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

import org.junit.Test;

public class DoubleUtilsTest {
   private static final NumberUtils<Double> UTILS = DoubleUtils.DOUBLE_UTILS;

   @Test
   public void testAddPrimitive() {
      assertEquals(Double.valueOf(9), UTILS.add(7d, 2d));
   }

   @Test
   public void testSubtractPrimitive() {
      assertEquals(Double.valueOf(5), UTILS.subtract(7d, 2d));
   }

   @Test
   public void testMultiplyPrimitive() {
      assertEquals(Double.valueOf(14), UTILS.multiply(7d, 2d));
   }

   @Test
   public void testDividePrimitive() {
      assertEquals(Double.valueOf(3.5), UTILS.divide(7d, 2d));
   }
}
