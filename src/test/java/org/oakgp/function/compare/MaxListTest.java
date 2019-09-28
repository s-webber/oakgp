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
package org.oakgp.function.compare;

import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.listType;

import java.util.Collections;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.node.ConstantNode;

public class MaxListTest extends AbstractFunctionTest {
   @Override
   protected MaxList getFunction() {
      return new MaxList();
   }

   @Override
   public void testEvaluate() {
      ConstantNode emptyList = new ConstantNode(Collections.emptyList(), listType(integerType()));
      evaluate("(max v0)").assigned(emptyList).to(null);
      evaluate("(max [7])").to(7);
      evaluate("(max [42 50])").to(50);
      evaluate("(max [8 4 7])").to(8);
      evaluate("(max [2 -6 -56 42])").to(42);
   }

   @Override
   public void testCanSimplify() {
      // TODO simplify("(max [2 -12 8])").to("8");

      simplify("(max (sort v0))").with(listType(integerType())).to("(max v0)");

      // TODO (max [v0 v1]) -> (max v0 v1)
      // TODO (max [v0 v1 v1]) -> (max v0 v1)
      // TODO (max [v2 v0 v1]) -> (max [v0 v1 v2])
      // TODO (max [v0 v1 v2 v1]) -> (max [v0 v1 v2])
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(max (set v0))", listType(integerType()));
      cannotSimplify("(max (sorted-set v0))", listType(integerType()));
   }
}
