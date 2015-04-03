package org.oakgp.serialize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.oakgp.function.Function;
import org.oakgp.function.math.Add;
import org.oakgp.function.math.Multiply;
import org.oakgp.function.math.Subtract;

public class SymbolMapTest {
   // TODO test getDisplayName when no symbol mapping exists for the specified Function

   private final SymbolMap symbolMap = new SymbolMap();

   @Test
   public void testGetFunctionBySymbol() {
      assertSame(Add.class, symbolMap.getFunction("+").getClass());
      assertSame(Subtract.class, symbolMap.getFunction("-").getClass());
      assertSame(Multiply.class, symbolMap.getFunction("*").getClass());
   }

   @Test
   public void testGetFunctionByClassName() {
      Function operator = symbolMap.getFunction(Add.class.getName());
      assertSame(Add.class, operator.getClass());
   }

   @Test
   public void testGetFunctionClassCastException() {
      try {
         symbolMap.getFunction("java.lang.String");
         fail();
      } catch (ClassCastException e) {
         assertEquals("java.lang.String cannot be cast to org.oakgp.function.Function", e.getMessage());
      }
   }

   @Test
   public void testGetFunctionClassDoesNotExist() {
      try {
         symbolMap.getFunction("a.made.up.class.Xyz");
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("Could not find class: a.made.up.class.Xyz", e.getMessage());
      }
   }

   @Test
   public void testGetFunctionSymbolDoesNotExist() {
      try {
         symbolMap.getFunction("^");
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("Could not find class: ^", e.getMessage());
      }
   }

   @Test
   public void testGetDisplayName() {
      assertEquals("+", symbolMap.getDisplayName(new Add()));
      assertEquals("-", symbolMap.getDisplayName(new Subtract()));
      assertEquals("*", symbolMap.getDisplayName(new Multiply()));
   }
}
