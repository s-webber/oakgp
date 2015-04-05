package org.oakgp;

import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.oakgp.TestUtils.assertUnmodifiable;
import static org.oakgp.TestUtils.booleanConstant;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerType;
import static org.oakgp.Type.stringType;

import java.util.List;

import org.junit.Test;
import org.oakgp.node.ConstantNode;

public class ConstantSetTest {
   @Test
   public void testGetByType() {
      ConstantNode c0 = integerConstant(7);
      ConstantNode c1 = booleanConstant(TRUE);
      ConstantNode c2 = integerConstant(5);

      ConstantSet s = new ConstantSet(c0, c1, c2);

      List<ConstantNode> integers = s.getByType(integerType());
      assertEquals(2, integers.size());
      assertSame(c0, integers.get(0));
      assertSame(c2, integers.get(1));

      List<ConstantNode> booleans = s.getByType(booleanType());
      assertEquals(1, booleans.size());
      assertSame(c1, booleans.get(0));

      assertNull(s.getByType(stringType())); // TODO expect empty list?
   }

   @Test
   public void assertGetByTypeUnmodifiable() {
      ConstantSet s = new ConstantSet(integerConstant(7), booleanConstant(TRUE), integerConstant(5));
      List<ConstantNode> integers = s.getByType(integerType());
      assertUnmodifiable(integers);
   }
}
