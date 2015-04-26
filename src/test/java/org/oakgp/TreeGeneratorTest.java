package org.oakgp;

import static org.oakgp.TestUtils.assertNodeEquals;
import static org.oakgp.TestUtils.integerConstant;

import org.junit.Test;
import org.oakgp.function.Function;
import org.oakgp.function.math.Add;
import org.oakgp.node.Node;
import org.oakgp.util.DummyPrimitiveSet;
import org.oakgp.util.DummyRandom;

public class TreeGeneratorTest {
   @Test
   public void testFull() {
      PrimitiveSet p = new DummyPrimitiveSet() {
         int terminalCtr = 1;

         @Override
         public Function nextFunction(Type type) {
            return new Add();
         }

         @Override
         public Node nextTerminal(Type type) {
            return integerConstant(terminalCtr++);
         }
      };
      TreeGenerator g = TreeGeneratorImpl.full(p);
      Node result = g.generate(Type.integerType(), 3);
      assertNodeEquals("(+ (+ (+ 1 2) (+ 3 4)) (+ (+ 5 6) (+ 7 8)))", result);
   }

   @Test
   public void testGrow() {
      PrimitiveSet p = new DummyPrimitiveSet() {
         int terminalCtr = 1;

         @Override
         public Function nextFunction(Type type) {
            return new Add();
         }

         @Override
         public Node nextTerminal(Type type) {
            return integerConstant(terminalCtr++);
         }
      };
      TreeGenerator g = TreeGeneratorImpl.grow(p, new DummyRandom(true, true, false, true, true, true, false));
      Node result = g.generate(Type.integerType(), 3);
      assertNodeEquals("(+ (+ 1 (+ 2 3)) (+ (+ 4 5) 6))", result);
   }
}
