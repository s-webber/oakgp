package org.oakgp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.oakgp.Signature.createSignature;
import static org.oakgp.TestUtils.assertUnmodifiable;
import static org.oakgp.Type.booleanArrayType;
import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerArrayType;
import static org.oakgp.Type.integerType;
import static org.oakgp.Type.stringType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.oakgp.function.Function;
import org.oakgp.function.classify.IsZero;
import org.oakgp.function.coll.Count;
import org.oakgp.function.math.Add;
import org.oakgp.function.math.Multiply;
import org.oakgp.function.math.Subtract;

public class FunctionSetTest {
   private static final List<Type> TWO_INTEGERS = Collections.unmodifiableList(Arrays.asList(integerType(), integerType()));

   @Test
   public void testGetFunctionBySymbol() {
      FunctionSet functionSet = FunctionSet.createDefaultFunctionSet();
      assertSame(Add.class, functionSet.getFunction("+", TWO_INTEGERS).getClass());
      assertSame(Subtract.class, functionSet.getFunction("-", TWO_INTEGERS).getClass());
      assertSame(Multiply.class, functionSet.getFunction("*", TWO_INTEGERS).getClass());
   }

   @Test
   public void testGetFunctionByClassName() {
      FunctionSet functionSet = FunctionSet.createDefaultFunctionSet();
      try {
         functionSet.getFunction(Add.class.getName(), TWO_INTEGERS);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("Could not find function: org.oakgp.function.math.Add", e.getMessage());
      }
   }

   @Test
   public void testGetFunctionSymbolDoesNotExist() {
      FunctionSet functionSet = FunctionSet.createDefaultFunctionSet();
      try {
         functionSet.getFunction("^", TWO_INTEGERS);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("Could not find function: ^", e.getMessage());
      }
   }

   @Test
   public void testGetFunctionTooFewTypes() {
      assertCannotFindByTypes(Arrays.asList(integerType()));
   }

   @Test
   public void testGetFunctionTooManyTypes() {
      assertCannotFindByTypes(Arrays.asList(integerType(), integerType(), integerType()));
   }

   @Test
   public void testGetFunctionWrongTypes() {
      assertCannotFindByTypes(Arrays.asList(integerType(), booleanType()));
   }

   private void assertCannotFindByTypes(List<Type> types) {
      FunctionSet functionSet = FunctionSet.createDefaultFunctionSet();
      try {
         functionSet.getFunction("+", types);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("Could not find version of function: + for: " + types, e.getMessage());
      }
   }

   @Test
   public void testGetByType() {
      Add add = new Add();
      Subtract subtract = new Subtract();
      Multiply multiply = new Multiply();
      IsZero isZero = new IsZero();
      FunctionSet functionSet = new FunctionSet(add, subtract, multiply, isZero);

      List<Function> integers = functionSet.getByType(integerType());
      assertEquals(3, integers.size());
      assertSame(add, integers.get(0));
      assertSame(subtract, integers.get(1));
      assertSame(multiply, integers.get(2));

      List<Function> booleans = functionSet.getByType(booleanType());
      assertEquals(1, booleans.size());
      assertSame(isZero, booleans.get(0));

      assertNull(functionSet.getByType(stringType())); // TODO expect empty list?
   }

   @Test
   public void assertGetByTypeUnmodifiable() {
      FunctionSet functionSet = FunctionSet.createDefaultFunctionSet();
      List<Function> integers = functionSet.getByType(integerType());
      assertUnmodifiable(integers);
   }

   @Test
   public void testGetBySignature() {
      Add add = new Add();
      Subtract subtract = new Subtract();
      Count countIntegerArray = new Count(integerType());
      Count countBooleanArray = new Count(booleanType());
      FunctionSet functionSet = new FunctionSet(add, subtract, countIntegerArray, countBooleanArray);

      // sanity check we have added 4 functions with a return type of integer
      assertEquals(4, functionSet.getByType(integerType()).size());

      List<Function> integers = functionSet.getBySignature(createSignature(integerType(), integerType(), integerType()));
      assertEquals(2, integers.size());
      assertSame(add, integers.get(0));
      assertSame(subtract, integers.get(1));

      List<Function> integerArrays = functionSet.getBySignature(createSignature(integerType(), integerArrayType()));
      assertEquals(1, integerArrays.size());
      assertSame(countIntegerArray, integerArrays.get(0));

      List<Function> booleanArrays = functionSet.getBySignature(createSignature(integerType(), booleanArrayType()));
      assertEquals(1, booleanArrays.size());
      assertSame(countBooleanArray, booleanArrays.get(0));

      assertNull(functionSet.getBySignature(createSignature(stringType(), integerType(), integerType()))); // TODO expect empty list?
   }

   @Test
   public void assertGetBySignatureUnmodifiable() {
      FunctionSet functionSet = FunctionSet.createDefaultFunctionSet();
      List<Function> integers = functionSet.getByType(integerType());
      assertUnmodifiable(integers);
   }
}
