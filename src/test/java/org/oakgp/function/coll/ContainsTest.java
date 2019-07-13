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

import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.listType;

import java.util.Collections;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.node.ConstantNode;

public class ContainsTest extends AbstractFunctionTest {
   @Override
   protected Contains getFunction() {
      return new Contains(integerType());
   }

   @Override
   public void testEvaluate() {
      evaluate("(contains [8 5 6] 8)").to(true);
      evaluate("(contains [8 5 6] 5)").to(true);
      evaluate("(contains [8 5 6] 6)").to(true);

      evaluate("(contains [8 5 6] 0)").to(false);
      evaluate("(contains [8 5 6] 1)").to(false);
      evaluate("(contains [8 5 6] 7)").to(false);
      evaluate("(contains [8 5 6] 42)").to(false);
      evaluate("(contains [8 5 6] -8)").to(false);

      ConstantNode emptyList = new ConstantNode(Collections.emptyList(), listType(integerType()));
      evaluate("(contains v0 1)").assigned(emptyList).to(false);
   }

   @Override
   public void testCanSimplify() {
      simplify("(contains [2 -12 8] -12)").to("true");
   }

   @Override
   public void testCannotSimplify() {
   }
}
