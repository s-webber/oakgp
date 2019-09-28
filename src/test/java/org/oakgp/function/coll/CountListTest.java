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

import static org.oakgp.type.CommonTypes.integerListType;
import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.listType;
import static org.oakgp.type.CommonTypes.mapType;

import java.util.Collections;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.node.ConstantNode;

public class CountListTest extends AbstractFunctionTest {
   private static final CountList count = new CountList();

   @Override
   protected CountList getFunction() {
      return count;
   }

   @Override
   public void testEvaluate() {
      ConstantNode emptyList = new ConstantNode(Collections.emptyList(), listType(integerType()));
      evaluate("(count v0)").assigned(emptyList).to(0);
      evaluate("(count [2 -12 8])").to(3);
      evaluate("(count [2 -12 8 -3 -7])").to(5);
   }

   @Override
   public void testCanSimplify() {
      simplify("(count [2 -12 8])").to("3");

      // with mapper functions
      simplify("(count (map zero? v0))").with(listType(integerType())).to("(count v0)");
      simplify("(count (sort v0))").with(listType(integerType())).to("(count v0)");
      simplify("(count (map zero? (set v0)))").with(listType(integerType())).to("(count (set v0))");

      // nested mapper functions
      simplify("(count (map false? (map zero? v0)))").with(listType(integerType())).to("(count v0)");
      simplify("(count (sort (map false? (map zero? v0))))").with(listType(integerType())).to("(count v0)");
      simplify("(count (map false? (sort (map zero? v0))))").with(listType(integerType())).to("(count v0)");
      simplify("(count (map false? (map zero? (sort v0))))").with(listType(integerType())).to("(count v0)");

      // nested mapper functions with variable of type Map rather than List
      simplify("(count (keys (map-values zero? v0)))").with(mapType(integerType(), integerType())).to("(count v0)");
      simplify("(count (values (map-values zero? v0)))").with(mapType(integerType(), integerType())).to("(count v0)");
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(count (set v0))", integerListType());
   }
}
