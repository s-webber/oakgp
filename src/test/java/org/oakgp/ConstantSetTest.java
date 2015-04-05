package org.oakgp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
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
      ConstantNode c1 = new ConstantNode(Boolean.TRUE, booleanType());
      ConstantNode c2 = integerConstant(5);

      ConstantSet s = new ConstantSet(c0, c1, c2);

      List<ConstantNode> integers = s.getByType(integerType());
      assertEquals(2, integers.size());
      assertSame(c0, integers.get(0));
      assertSame(c2, integers.get(1));

      List<ConstantNode> strings = s.getByType(booleanType());
      assertEquals(1, strings.size());
      assertSame(c1, strings.get(0));

      assertNull(s.getByType(stringType())); // TODO expect empty list?
   }
}
