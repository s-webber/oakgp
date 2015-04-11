package org.oakgp.function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.Type;

public class FunctionTest {
   @Test
   public void testSimplify() {
      Function o = new Function() {
         @Override
         public Signature getSignature() {
            throw new UnsupportedOperationException();
         }

         @Override
         public Object evaluate(Arguments arguments, Assignments assignments) {
            throw new UnsupportedOperationException();
         }
      };
      assertNull(o.simplify(null));
   }

   @Test
   public void testGetDisplayName() {
      assertEquals("dummyfunction", new DummyFunction().getDisplayName());
   }

   @Test
   public void testGetDisplayNameBooleanReturnType() {
      assertEquals("booleandummyfunction?", new IsBooleanDummyFunction().getDisplayName());
   }
}

class DummyFunction implements Function {
   @Override
   public Signature getSignature() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      throw new UnsupportedOperationException();
   }
}

class IsBooleanDummyFunction implements Function {
   @Override
   public Signature getSignature() {
      return Signature.createSignature(Type.booleanType(), Type.integerType());
   }

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      throw new UnsupportedOperationException();
   }
}
