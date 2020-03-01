/*
 * Copyright 2019 S. Webber
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
package org.oakgp.function;

import static org.junit.Assert.assertEquals;
import static org.oakgp.TestUtils.readNode;

import java.util.Collections;

import org.junit.Test;
import org.oakgp.Arguments;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

public class BooleanFunctionTest {
   @Test
   public void testDefaults() {
      BooleanFunction f = new BooleanFunction() {

         @Override
         public Signature getSignature() {
            throw new UnsupportedOperationException();
         }

         @Override
         public Object evaluate(Arguments arguments) {
            throw new UnsupportedOperationException();
         }
      };

      FunctionNode fn = (FunctionNode) readNode("(+ v0 v1)");
      Node n = readNode("v0");
      assertEquals(Collections.emptySet(), f.getCauses(fn));
      assertEquals(Collections.emptySet(), f.getConsequences(fn));
      assertEquals(Collections.emptySet(), f.getIncompatibles(fn));
      assertEquals(null, f.getOpposite(fn));
      assertEquals(null, f.getUnion(fn, n));
   }
}
