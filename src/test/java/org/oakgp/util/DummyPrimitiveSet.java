package org.oakgp.util;

import org.oakgp.PrimitiveSet;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.node.Node;

public class DummyPrimitiveSet implements PrimitiveSet {
   @Override
   public boolean hasTerminals(Type type) {
      return true;
   }

   @Override
   public boolean hasFunctions(Type type) {
      return true;
   }

   @Override
   public Node nextTerminal(Type type) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Node nextAlternativeTerminal(Node current) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Function nextFunction(Type type) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Function nextAlternativeFunction(Function current) {
      throw new UnsupportedOperationException();
   }
}
