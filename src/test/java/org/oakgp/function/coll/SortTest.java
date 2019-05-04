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

import static org.oakgp.Type.integerType;

import java.util.Arrays;
import java.util.Collections;

import org.oakgp.Type;
import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.node.ConstantNode;

public class SortTest extends AbstractFunctionTest {
   @Override
   protected Sort getFunction() {
      return new Sort(integerType());
   }

   @Override
   public void testEvaluate() {
      ConstantNode emptyList = new ConstantNode(Collections.emptyList(), Type.listType(Type.integerType()));
      evaluate("(sort v0)").assigned(emptyList).to(Collections.emptyList());
      evaluate("(sort [2 5 3 6 9 8 2 7])").to(Arrays.asList(2, 2, 3, 5, 6, 7, 8, 9));
      evaluate("(sort [2 2 3 5 6 7 8 9])").to(Arrays.asList(2, 2, 3, 5, 6, 7, 8, 9));
   }

   @Override
   public void testCanSimplify() {
      simplify("(sort (sort v0))").with(Type.listType(Type.integerType())).to("(sort v0)");
      simplify("(sort [7 8 6])").to("[6 7 8]");
   }

   @Override
   public void testCannotSimplify() {
   }
}
