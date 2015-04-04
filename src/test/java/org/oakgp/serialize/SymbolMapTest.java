package org.oakgp.serialize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.oakgp.Type;
import org.oakgp.function.math.Add;
import org.oakgp.function.math.Multiply;
import org.oakgp.function.math.Subtract;

public class SymbolMapTest {
   // TODO test getDisplayName when no symbol mapping exists for the specified Function

   private final SymbolMap symbolMap = SymbolMap.createDefaultSymbolMap();
   private final List<Type> types = Collections.unmodifiableList(Arrays.asList(integerType(), integerType()));

   @Test
   public void testGetDisplayName() {
      assertEquals("+", symbolMap.getDisplayName(new Add()));
      assertEquals("-", symbolMap.getDisplayName(new Subtract()));
      assertEquals("*", symbolMap.getDisplayName(new Multiply()));
   }

   @Test
   public void testGetFunctionBySymbol() {
      assertSame(Add.class, symbolMap.getFunction("+", types).getClass());
      assertSame(Subtract.class, symbolMap.getFunction("-", types).getClass());
      assertSame(Multiply.class, symbolMap.getFunction("*", types).getClass());
   }

   @Test
   public void testGetFunctionByClassName() {
      try {
         symbolMap.getFunction(Add.class.getName(), types);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("Could not find function: org.oakgp.function.math.Add", e.getMessage());
      }
   }

   @Test
   public void testGetFunctionSymbolDoesNotExist() {
      try {
         symbolMap.getFunction("^", types);
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
      try {
         symbolMap.getFunction("+", types);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("Could not find version of function: + for: " + types, e.getMessage());
      }
   }
}
