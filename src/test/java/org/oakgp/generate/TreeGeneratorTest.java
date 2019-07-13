/*
 * Copyright 2015 S. Webber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oakgp.generate;

import static org.oakgp.TestUtils.assertNodeEquals;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.function.math.IntegerUtils.INTEGER_UTILS;
import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.Types.declareType;
import static org.oakgp.type.Types.type;
import static org.oakgp.util.DummyRandom.random;

import org.junit.Test;
import org.oakgp.Arguments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.primitive.ConstantSet;
import org.oakgp.primitive.DummyPrimitiveSet;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.primitive.PrimitiveSet;
import org.oakgp.primitive.PrimitiveSetImpl;
import org.oakgp.primitive.VariableSet;
import org.oakgp.type.Types.Type;
import org.oakgp.util.DummyRandom;

public class TreeGeneratorTest {
   @Test
   public void testFull() {
      PrimitiveSet p = createPrimitiveSet();
      TreeGenerator g = TreeGeneratorImpl.full(p);
      Node result = g.generate(integerType(), 3);
      assertNodeEquals("(+ (+ (+ 1 2) (+ 3 4)) (+ (+ 5 6) (+ 7 8)))", result);
   }

   @Test
   public void testGrow() {
      PrimitiveSet p = createPrimitiveSet();
      TreeGenerator g = TreeGeneratorImpl.grow(p, random().setBooleans(true, true, false, true, true, true, false).build());
      Node result = g.generate(integerType(), 3);
      assertNodeEquals("(+ (+ 1 (+ 2 3)) (+ (+ 4 5) 6))", result);
   }

   /**
    * Tests building a tree when the return types of the elements of the primitive set force a specific result.
    * <p>
    * i.e. In this test there is only one function or terminal node for each of the required types.
    */
   @Test
   public void testWhenTypesEnforceStructure() {
      Type a = declareType("a");
      Type b = declareType("b");
      Type c = declareType("c");
      Type d = declareType("d");

      Function f1 = createFunction("f1", a, b);
      Function f2 = createFunction("f2", b, c, d);
      ConstantNode cn = new ConstantNode("X", c);

      DummyRandom random = random().setDoubles(1d, 1d).build();
      PrimitiveSet p = new PrimitiveSetImpl(new FunctionSet(f1, f2), new ConstantSet(cn), VariableSet.createVariableSet(type("d")), random, .5);
      TreeGenerator g = TreeGeneratorImpl.full(p);
      Node result = g.generate(type("a"), 3);
      assertNodeEquals("(f1 (f2 \"X\" v0))", result);
   }

   private PrimitiveSet createPrimitiveSet() {
      PrimitiveSet p = new DummyPrimitiveSet() {
         int terminalCtr = 1;

         @Override
         public Function nextFunction(Type type) {
            return INTEGER_UTILS.getAdd();
         }

         @Override
         public Node nextTerminal(Type type) {
            return integerConstant(terminalCtr++);
         }
      };
      return p;
   }

   private Function createFunction(String displayName, Type returnType, Type... arguments) {
      return new Function() {
         @Override
         public Object evaluate(Arguments arguments) {
            throw new UnsupportedOperationException();
         }

         @Override
         public Signature getSignature() {
            return Signature.createSignature(returnType, arguments);
         }

         @Override
         public String getDisplayName() {
            return displayName;
         }
      };
   }
}
