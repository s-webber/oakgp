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
import static org.oakgp.type.CommonTypes.mapType;

import java.util.Collections;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.node.ConstantNode;

public class CountMapTest extends AbstractFunctionTest {
   @Override
   protected CountMap getFunction() {
      return CountMap.getSingleton();
   }

   @Override
   public void testEvaluate() {
      ConstantNode emptyMap = new ConstantNode(Collections.emptyMap(), mapType(integerType(), integerType()));
      evaluate("(count v0)").assigned(emptyMap).to(0);
      evaluate("(count {1 2})").to(1);
      evaluate("(count {1 2 3 4 5 6})").to(3);
   }

   @Override
   public void testCanSimplify() {
      simplify("(count {1 2 3 4 5 6})").to("3");

      // with mapper functions
      simplify("(count (map-values zero? v0))").with(mapType(integerType(), integerType())).to("(count v0)");

      // nested mapper functions
      simplify("(count (map-values false? (map-values zero? v0)))").with(mapType(integerType(), integerType())).to("(count v0)");
   }

   @Override
   public void testCannotSimplify() {
   }
}
