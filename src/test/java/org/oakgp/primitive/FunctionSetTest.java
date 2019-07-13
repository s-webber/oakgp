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
import static org.oakgp.type.CommonTypes.integerListType;
import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.stringType;

import java.util.List;

import org.junit.Test;
import org.oakgp.function.Function;
import org.oakgp.function.choice.If;
import org.oakgp.function.classify.IsNegative;
import org.oakgp.function.classify.IsPositive;
import org.oakgp.function.classify.IsZero;
import org.oakgp.function.coll.Count;
import org.oakgp.function.compare.Equal;
import org.oakgp.function.compare.GreaterThan;
import org.oakgp.function.compare.GreaterThanOrEqual;
import org.oakgp.function.compare.LessThan;
import org.oakgp.function.compare.LessThanOrEqual;
import org.oakgp.function.compare.NotEqual;
import org.oakgp.function.hof.Filter;
import org.oakgp.function.hof.Reduce;
import org.oakgp.function.math.IntegerUtils;

public class FunctionSetTest {
   private static final Function ADD = IntegerUtils.INTEGER_UTILS.getAdd();
   private static final Function SUBTRACT = IntegerUtils.INTEGER_UTILS.getSubtract();
   private static final Function MULTIPLY = IntegerUtils.INTEGER_UTILS.getMultiply();

   @Test
   public void testGetByType() {
      IsZero isZero = new IsZero();
      FunctionSet functionSet = new FunctionSet(ADD, SUBTRACT, MULTIPLY, isZero);

      List<Function> integers = functionSet.getByType(integerType());
      assertEquals(3, integers.size());
      assertSame(ADD, integers.get(0));
      assertSame(SUBTRACT, integers.get(1));
      assertSame(MULTIPLY, integers.get(2));

      List<Function> booleans = functionSet.getByType(booleanType());
      assertEquals(1, booleans.size());
      assertSame(isZero, booleans.get(0));

      assertNull(functionSet.getByType(stringType()));
   }

   @Test
   public void assertGetByTypeUnmodifiable() {
      FunctionSet functionSet = createFunctionSet();
      List<Function> integers = functionSet.getByType(integerType());
      assertUnmodifiable(integers);
   }

   @Test
   public void testGetBySignature() {
      Count countIntegerArray = new Count(integerType());
      Count countBooleanArray = new Count(booleanType());
      FunctionSet functionSet = new FunctionSet(ADD, SUBTRACT, countIntegerArray, countBooleanArray);

      // sanity check we have added 4 functions with a return type of integer
      assertEquals(4, functionSet.getByType(integerType()).size());

      List<Function> integers = functionSet.getBySignature(createSignature(integerType(), integerType(), integerType()));
      assertEquals(2, integers.size());
      assertSame(ADD, integers.get(0));
      assertSame(SUBTRACT, integers.get(1));

      List<Function> integerArrays = functionSet.getBySignature(createSignature(integerType(), integerListType()));
      assertEquals(1, integerArrays.size());
      assertSame(countIntegerArray, integerArrays.get(0));

      List<Function> booleanArrays = functionSet.getBySignature(createSignature(integerType(), booleanListType()));
      assertEquals(1, booleanArrays.size());
      assertSame(countBooleanArray, booleanArrays.get(0));

      assertNull(functionSet.getBySignature(createSignature(stringType(), integerType(), integerType())));
   }

   @Test
   public void assertGetBySignatureUnmodifiable() {
      FunctionSet functionSet = createFunctionSet();
      List<Function> integers = functionSet.getBySignature(createSignature(integerType(), integerType(), integerType()));
      assertUnmodifiable(integers);
   }

   private static FunctionSet createFunctionSet() {
      return new FunctionSet(
            // arithmetic
            ADD, SUBTRACT, MULTIPLY,
            // comparison
            LessThan.create(integerType()), LessThanOrEqual.create(integerType()), new GreaterThan(integerType()), new GreaterThanOrEqual(integerType()),
            new Equal(integerType()), new NotEqual(integerType()),
            // selection
            new If(integerType()),
            // higher-order functions
            new Reduce(integerType()), new Filter(integerType()), new org.oakgp.function.hof.Map(integerType(), booleanType()),
            // classify
            new IsPositive(), new IsNegative(), new IsZero(),
            // collections
            new Count(integerType()), new Count(booleanType()));
   }
}
