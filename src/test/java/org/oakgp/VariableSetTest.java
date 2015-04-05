package org.oakgp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.oakgp.TestUtils.assertUnmodifiable;
import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerType;
import static org.oakgp.Type.stringType;

import java.util.List;

import org.junit.Test;
import org.oakgp.node.VariableNode;

public class VariableSetTest {
   @Test
   public void testGetById() {
      VariableSet s = VariableSet.createVariableSet(integerType(), booleanType(), integerType());

      VariableNode v0 = s.getById(0);
      VariableNode v1 = s.getById(1);
      VariableNode v2 = s.getById(2);

      assertSame(v0, s.getById(0));
      assertSame(integerType(), v0.getType());

      assertSame(v1, s.getById(1));
      assertSame(booleanType(), v1.getType());

      assertSame(v2, s.getById(2));
      assertSame(integerType(), v2.getType());

      assertNotEquals(v0, v1);
      assertNotEquals(v0, v2);
      assertNotEquals(v1, v2);
   }

   @Test
   public void assertGetByType() {
      VariableSet s = VariableSet.createVariableSet(integerType(), booleanType(), integerType());

      VariableNode v0 = s.getById(0);
      VariableNode v1 = s.getById(1);
      VariableNode v2 = s.getById(2);

      List<VariableNode> integers = s.getByType(integerType());
      assertEquals(2, integers.size());
      assertSame(v0, integers.get(0));
      assertSame(v2, integers.get(1));

      List<VariableNode> booleans = s.getByType(booleanType());
      assertEquals(1, booleans.size());
      assertSame(v1, booleans.get(0));

      assertNull(s.getByType(stringType())); // TODO expect empty list?
   }

   @Test
   public void assertGetByTypeUnmodifiable() {
      VariableSet s = VariableSet.createVariableSet(integerType(), booleanType(), integerType());
      List<VariableNode> integers = s.getByType(integerType());
      assertUnmodifiable(integers);
   }
}
