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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.oakgp.TestUtils.assertUnmodifiable;
import static org.oakgp.function.Signature.createSignature;
import static org.oakgp.type.CommonTypes.booleanListType;
import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.doubleType;
import static org.oakgp.type.CommonTypes.integerListType;
import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.stringType;

import java.util.List;

import org.junit.Test;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.function.choice.If;
import org.oakgp.function.classify.IsNegative;
import org.oakgp.function.classify.IsPositive;
import org.oakgp.function.classify.IsZero;
import org.oakgp.function.coll.CountList;
import org.oakgp.function.compare.Equal;
import org.oakgp.function.compare.GreaterThan;
import org.oakgp.function.compare.GreaterThanOrEqual;
import org.oakgp.function.compare.LessThan;
import org.oakgp.function.compare.LessThanOrEqual;
import org.oakgp.function.compare.NotEqual;
import org.oakgp.function.hof.Map;
import org.oakgp.function.hof.Reduce;
import org.oakgp.function.math.BigDecimalUtils;
import org.oakgp.function.math.DoubleUtils;
import org.oakgp.function.math.IntegerUtils;
import org.oakgp.util.FunctionSetBuilder;

public class FunctionSetTest {
   private static final Function ADD = IntegerUtils.INTEGER_UTILS.getAdd();
   private static final Function SUBTRACT = IntegerUtils.INTEGER_UTILS.getSubtract();
   private static final Function MULTIPLY = IntegerUtils.INTEGER_UTILS.getMultiply();

   @Test
   public void testGetByType() {
      IsZero isZero = new IsZero();
      FunctionSet functionSet = new FunctionSetBuilder().add(ADD).add(SUBTRACT).add(MULTIPLY).add(isZero).build();

      List<FunctionSet.Key> integers = functionSet.getByType(integerType());
      assertEquals(3, integers.size());
      assertSame(ADD, integers.get(0).getFunction());
      assertSame(SUBTRACT, integers.get(1).getFunction());
      assertSame(MULTIPLY, integers.get(2).getFunction());

      List<FunctionSet.Key> booleans = functionSet.getByType(booleanType());
      assertEquals(1, booleans.size());
      assertSame(isZero, booleans.get(0).getFunction());

      assertNull(functionSet.getByType(stringType()));
   }

   @Test
   public void assertGetByTypeUnmodifiable() {
      FunctionSet functionSet = createFunctionSet();
      List<FunctionSet.Key> integers = functionSet.getByType(integerType());
      assertUnmodifiable(integers);
   }

   @Test
   public void testGetBySignature() {
      CountList count = new CountList();
      FunctionSet functionSet = new FunctionSetBuilder().add(ADD).add(SUBTRACT).add(count, integerType()).add(count, booleanType()).build();

      // sanity check we have added 4 functions with a return type of integer
      assertEquals(4, functionSet.getByType(integerType()).size());

      List<FunctionSet.Key> integers = functionSet.getBySignature(createSignature(integerType(), integerType(), integerType()));
      assertEquals(2, integers.size());
      assertSame(ADD, integers.get(0).getFunction());
      assertSame(SUBTRACT, integers.get(1).getFunction());

      List<FunctionSet.Key> integerArrays = functionSet.getBySignature(createSignature(integerType(), integerListType()));
      assertEquals(1, integerArrays.size());
      assertSame(count, integerArrays.get(0).getFunction());
      assertEquals(count.getSignature().create(integerType()), integerArrays.get(0).getSignature());

      List<FunctionSet.Key> booleanArrays = functionSet.getBySignature(createSignature(integerType(), booleanListType()));
      assertEquals(1, booleanArrays.size());
      assertSame(count, booleanArrays.get(0).getFunction());
      assertEquals(count.getSignature().create(booleanType()), booleanArrays.get(0).getSignature());

      assertNull(functionSet.getBySignature(createSignature(stringType(), integerType(), integerType())));
   }

   @Test
   public void testGetBySignatureUnmodifiable() {
      FunctionSet functionSet = createFunctionSet();
      List<FunctionSet.Key> integers = functionSet.getBySignature(createSignature(integerType(), integerType(), integerType()));
      assertUnmodifiable(integers);
   }

   @Test
   public void testGetFunctions_sameFunctionNamesDifferentSignatures() {
      Function addIntegers = IntegerUtils.INTEGER_UTILS.getAdd();
      Function addDoubles = DoubleUtils.DOUBLE_UTILS.getAdd();
      Function addBigDecimals = BigDecimalUtils.BIG_DECIMAL_UTILS.getAdd();
      FunctionSet functionSet = new FunctionSetBuilder().add(addIntegers).add(addDoubles).add(addBigDecimals).build();

      List<FunctionSet.Key> functions = functionSet.getFunctions();

      assertEquals(3, functions.size());
      assertSame(addIntegers, functions.get(0).getFunction());
      assertSame(addIntegers.getSignature(), functions.get(0).getSignature());
      assertSame(addDoubles, functions.get(1).getFunction());
      assertSame(addDoubles.getSignature(), functions.get(1).getSignature());
      assertSame(addBigDecimals, functions.get(2).getFunction());
      assertSame(addBigDecimals.getSignature(), functions.get(2).getSignature());
   }

   @Test
   public void testGetFunctions_sameReturnTypeDifferentSignatures() {
      Function addIntegers = IntegerUtils.INTEGER_UTILS.getAdd();
      IsZero isZero = new IsZero();
      FunctionSet functionSet = new FunctionSetBuilder().add(addIntegers).add(isZero).build();

      List<FunctionSet.Key> functions = functionSet.getFunctions();

      assertEquals(2, functions.size());
      assertSame(addIntegers, functions.get(0).getFunction());
      assertSame(addIntegers.getSignature(), functions.get(0).getSignature());
      assertSame(isZero, functions.get(1).getFunction());
      assertSame(isZero.getSignature(), functions.get(1).getSignature());
   }

   @Test
   public void testGetFunctions_sameFunctionDifferentSignatures() {
      Map function = Map.getSingleton();
      Signature signature1 = function.getSignature().create(integerType(), booleanType());
      Signature signature2 = function.getSignature().create(stringType(), doubleType());
      FunctionSet functionSet = new FunctionSetBuilder().add(function, integerType(), booleanType()).add(function, stringType(), doubleType()).build();

      List<FunctionSet.Key> functions = functionSet.getFunctions();

      assertEquals(2, functions.size());
      assertSame(function, functions.get(0).getFunction());
      assertEquals(signature1, functions.get(0).getSignature());
      assertSame(function, functions.get(1).getFunction());
      assertEquals(signature2, functions.get(1).getSignature());
   }

   private static FunctionSet createFunctionSet() {
      CountList count = new CountList();
      If ifFunction = new If();
      return new FunctionSetBuilder()
            // arithmetic
            .add(ADD).add(SUBTRACT).add(MULTIPLY)
            // comparison
            .add(LessThan.getSingleton(), integerType()).add(LessThanOrEqual.getSingleton(), integerType()).add(new GreaterThan(), integerType())
            .add(new GreaterThanOrEqual(), integerType()).add(new Equal(), integerType()).add(new NotEqual(), integerType())
            // selection
            .add(ifFunction, integerType())
            // higher-order functions
            .add(new Reduce(integerType())) // TODO .add(new Filter(integerType()))// TODO .add(new org.oakgp.function.hof.Map(integerType(), booleanType()))
            // classify
            .add(new IsPositive()).add(new IsNegative()).add(new IsZero())
            // collections
            .add(count, integerType()).add(count, booleanType())
            // construct
            .build();
   }
}
