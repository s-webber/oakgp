package org.oakgp.serialize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.oakgp.operator.Operator;
import org.oakgp.operator.math.Add;
import org.oakgp.operator.math.Multiply;
import org.oakgp.operator.math.Subtract;

public class SymbolMapTest {
   // TODO test getDisplayName when no symbol mapping exists for the specified Operator

   private final SymbolMap symbolMap = new SymbolMap();

   @Test
   public void testGetOperatorBySymbol() {
      assertSame(Add.class, symbolMap.getOperator("+").getClass());
      assertSame(Subtract.class, symbolMap.getOperator("-").getClass());
      assertSame(Multiply.class, symbolMap.getOperator("*").getClass());
   }

   @Test
   public void testGetOperatorByClassName() {
      Operator operator = symbolMap.getOperator(Add.class.getName());
      assertSame(Add.class, operator.getClass());
   }

   @Test
   public void testGetOperatorClassCastException() {
      try {
         symbolMap.getOperator("java.lang.String");
         fail();
      } catch (ClassCastException e) {
         assertEquals("java.lang.String cannot be cast to org.oakgp.operator.Operator", e.getMessage());
      }
   }

   @Test
   public void testGetOperatorClassDoesNotExist() {
      try {
         symbolMap.getOperator("a.made.up.class.Xyz");
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("Could not find class: a.made.up.class.Xyz", e.getMessage());
      }
   }

   @Test
   public void testGetOperatorSymbolDoesNotExist() {
      try {
         symbolMap.getOperator("^");
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
