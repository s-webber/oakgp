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
import static org.oakgp.TestUtils.bigIntegerConstant;
import static org.oakgp.TestUtils.createVariable;

import java.math.BigInteger;

import org.junit.Test;
import org.oakgp.Assignments;

public class BigIntegerUtilsTest {
   private static final NumberUtils<BigInteger> UTILS = BigIntegerUtils.BIG_INTEGER_UTILS;

   @Test
   public void testAddPrimitive() {
      assertEquals(BigInteger.valueOf(9), UTILS.add(BigInteger.valueOf(7), BigInteger.valueOf(2)));
   }

   @Test
   public void testAddNode() {
      assertEquals(BigInteger.valueOf(9), UTILS.add(bigIntegerConstant("7"), createVariable(0), Assignments.createAssignments(BigInteger.valueOf(2))));
   }

   @Test
   public void testSubtractPrimitive() {
      assertEquals(BigInteger.valueOf(5), UTILS.subtract(BigInteger.valueOf(7), BigInteger.valueOf(2)));
   }

   @Test
   public void testSubtractNode() {
      assertEquals(BigInteger.valueOf(5), UTILS.subtract(bigIntegerConstant("7"), createVariable(0), Assignments.createAssignments(BigInteger.valueOf(2))));
   }

   @Test
   public void testMultiplyPrimitive() {
      assertEquals(BigInteger.valueOf(14), UTILS.multiply(BigInteger.valueOf(7), BigInteger.valueOf(2)));
   }

   @Test
   public void testMultiplyNode() {
      assertEquals(BigInteger.valueOf(14), UTILS.multiply(bigIntegerConstant("7"), createVariable(0), Assignments.createAssignments(BigInteger.valueOf(2))));
   }

   @Test
   public void testDividePrimitive() {
      assertEquals(BigInteger.valueOf(3), UTILS.divide(BigInteger.valueOf(7), BigInteger.valueOf(2)));
   }

   @Test
   public void testDivideNode() {
      assertEquals(BigInteger.valueOf(3), UTILS.divide(bigIntegerConstant("7"), createVariable(0), Assignments.createAssignments(BigInteger.valueOf(2))));
   }
}
