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
package org.oakgp.function.classify;

import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.listType;

import java.util.Collections;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.node.ConstantNode;

public class IsDistinctTest extends AbstractFunctionTest {
   @Override
   protected IsDistinct getFunction() {
      return new IsDistinct();
   }

   @Override
   public void testEvaluate() {
      ConstantNode emptyList = new ConstantNode(Collections.emptyList(), listType(integerType()));
      evaluate("(distinct? v0)").assigned(emptyList).to(true);
      evaluate("(distinct? [7])").to(true);
      evaluate("(distinct? [7 7])").to(false);
      evaluate("(distinct? [8 4 7])").to(true);
      evaluate("(distinct? [8 4 8])").to(false);
   }

   @Override
   public void testCanSimplify() {
      simplify("(distinct? (set v0))").with(listType(integerType())).to("true");
      simplify("(distinct? (sorted-set v0))").with(listType(integerType())).to("true");
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(distinct? v0)", listType(integerType()));
   }
}
