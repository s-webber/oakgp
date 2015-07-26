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

public class IntegerUtilsTest {
   private static final NumberUtils<Integer> UTILS = IntegerUtils.INTEGER_UTILS;

   @Test
   public void testAdd() {
      assertEquals(Integer.valueOf(9), UTILS.add(7, 2));
   }

   @Test
   public void testSubtract() {
      assertEquals(Integer.valueOf(5), UTILS.subtract(7, 2));
   }

   @Test
   public void testMultiply() {
      assertEquals(Integer.valueOf(14), UTILS.multiply(7, 2));
   }

   @Test
   public void testDivide() {
      assertEquals(Integer.valueOf(3), UTILS.divide(7, 2));
   }
}
