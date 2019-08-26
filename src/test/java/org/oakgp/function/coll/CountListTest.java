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

import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.listType;
import static org.oakgp.type.CommonTypes.mapType;

import java.util.Collections;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.classify.IsFalse;
import org.oakgp.function.classify.IsZero;
import org.oakgp.function.hof.Map;
import org.oakgp.function.hof.MapValues;
import org.oakgp.node.ConstantNode;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.util.FunctionSetBuilder;

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

      // nested mapper functions
      simplify("(count (map false? (map zero? v0)))").with(listType(integerType())).to("(count v0)");
      simplify("(count (map false? (map zero? (sort v0))))").with(listType(integerType())).to("(count v0)");

      // nested mapper functions with variable of type Map rather than List
      simplify("(count (keys (map-values zero? v0)))").with(mapType(integerType(), integerType())).to("(count v0)");
      simplify("(count (values (map-values zero? v0)))").with(mapType(integerType(), integerType())).to("(count v0)");
   }

   @Override
   public void testCannotSimplify() {
   }

   @Override
   protected FunctionSet getFunctionSet() {
      return new FunctionSetBuilder().add(getFunction(), integerType()).add(getFunction(), booleanType()).add(new Map(), booleanType(), integerType())
            .add(Sort.getSingleton(), integerType()).add(new MapValues(), integerType(), booleanType(), integerType())
            .add(new Map(), booleanType(), booleanType()).add(CountMap.getSingleton(), integerType(), integerType())
            .add(new Values(), booleanType(), integerType()).add(new Keys(), integerType(), booleanType()).add(new IsZero()).add(new IsFalse()).build();
   }
}
