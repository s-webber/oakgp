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
import org.oakgp.function.coll.Set;
import org.oakgp.function.coll.Sort;
import org.oakgp.function.coll.SortedSet;
import org.oakgp.node.ConstantNode;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.util.FunctionSetBuilder;

public class MinListTest extends AbstractFunctionTest {
   @Override
   protected MinList getFunction() {
      return new MinList();
   }

   @Override
   public void testEvaluate() {
      ConstantNode emptyList = new ConstantNode(Collections.emptyList(), listType(integerType()));
      evaluate("(min v0)").assigned(emptyList).to(null);
      evaluate("(min [7])").to(7);
      evaluate("(min [42 50])").to(42);
      evaluate("(min [8 4 7])").to(4);
      evaluate("(min [2 -6 -56 42])").to(-56);
   }

   @Override
   public void testCanSimplify() {
      // TODO simplify("(min [2 -12 8])").to("8");

      simplify("(min (sort v0))").with(listType(integerType())).to("(min v0)");
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(min (set v0))", listType(integerType()));
      cannotSimplify("(min (sorted-set v0))", listType(integerType()));
   }

   @Override
   protected FunctionSet getFunctionSet() {
      return new FunctionSetBuilder().add(getFunction(), integerType()).add(Sort.getSingleton(), integerType()).add(Set.getSingleton(), integerType())
            .add(SortedSet.getSingleton(), integerType()).build();
   }
}
