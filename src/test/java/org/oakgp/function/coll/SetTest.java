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
package org.oakgp.function.coll;

import static org.oakgp.TestUtils.asSet;
import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.listType;

import java.util.Collections;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.node.ConstantNode;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.util.FunctionSetBuilder;

public class SetTest extends AbstractFunctionTest {
   @Override
   protected Set getFunction() {
      return new Set();
   }

   @Override
   public void testEvaluate() {
      ConstantNode emptyList = new ConstantNode(Collections.emptyList(), listType(integerType()));
      evaluate("(set v0)").assigned(emptyList).to(asSet());
      evaluate("(set [7])").to(asSet(7));
      evaluate("(set [8 4 7])").to(asSet(8, 4, 7));
   }

   @Override
   public void testCanSimplify() {
      simplify("(set (set v0))").with(listType(integerType())).to("(set v0)");
      simplify("(set (sorted-set v0))").with(listType(integerType())).to("(sorted-set v0)");
      simplify("(set (sort v0))").with(listType(integerType())).to("(sorted-set v0)");
   }

   @Override
   public void testCannotSimplify() {
   }

   @Override
   protected FunctionSet getFunctionSet() {
      return new FunctionSetBuilder().add(getFunction(), integerType()).add(SortedSet.getSingleton(), integerType()).add(Sort.getSingleton(), integerType())
            .build();
   }
}
