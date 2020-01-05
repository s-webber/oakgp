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
package org.oakgp.function.math;

import static org.oakgp.type.CommonTypes.doubleType;
import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.listType;

import java.util.Collections;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.classify.IsPositive;
import org.oakgp.function.coll.Set;
import org.oakgp.function.coll.Sort;
import org.oakgp.function.coll.SortedSet;
import org.oakgp.function.hof.Filter;
import org.oakgp.function.hof.Map;
import org.oakgp.node.ConstantNode;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.util.FunctionSetBuilder;

public class SumTest extends AbstractFunctionTest {
   private static final Filter FILTER = new Filter();
   private static final Map MAP = Map.getSingleton();
   private static final IsPositive IS_POSITIVE = IsPositive.getSingleton();

   @Override
   protected Sum<Integer> getFunction() {
      return IntegerUtils.INTEGER_UTILS.getSum();
   }

   @Override
   public void testEvaluate() {
      ConstantNode emptyList = new ConstantNode(Collections.emptyList(), listType(integerType()));
      evaluate("(sum v0)").assigned(emptyList).to(0);
      evaluate("(sum [7])").to(7);
      evaluate("(sum [42 50])").to(92);
      evaluate("(sum [8 4 7])").to(19);
      evaluate("(sum [2 -12 8])").to(-2);
   }

   @Override
   public void testCanSimplify() {
      simplify("(sum [2 -12 8])").to("-2");

      simplify("(sum (sort v0))").with(listType(integerType())).to("(sum v0)");

      simplify("(sum (sorted-set v0))").with(listType(integerType())).to("(sum (set v0))");

      simplify("(sum (filter pos? (sort v0)))").with(listType(integerType())).to("(sum (filter pos? v0))");
      simplify("(sum (filter pos? (sorted-set v0)))").with(listType(integerType())).to("(sum (set (filter pos? v0)))");
      simplify("(sum (filter pos? (set v0)))").with(listType(integerType())).to("(sum (set (filter pos? v0)))");

      simplify("(sum (map log (sort v0)))").with(listType(doubleType())).to("(sum (map log v0))");
      simplify("(sum (map log (map log (sort v0))))").with(listType(doubleType())).to("(sum (map log (map log v0)))");
      simplify("(sum (map log (map log (map log (map log (map log (sort v0)))))))").with(listType(doubleType()))
            .to("(sum (map log (map log (map log (map log (map log v0))))))");
      simplify("(sum (map log (map log (map log (sort (map log (sort (map log (sort v0)))))))))").with(listType(doubleType()))
            .to("(sum (map log (map log (map log (map log (map log v0))))))");
      simplify("(sum (sort (map log (sort (map log (sort (map log (sort (map log (sort (map log (sort v0))))))))))))").with(listType(doubleType()))
            .to("(sum (map log (map log (map log (map log (map log v0))))))");

      simplify("(sum (map log (sorted-set v0)))").with(listType(doubleType())).to("(sum (map log (set v0)))");
      simplify("(sum (map log (map log (sorted-set v0))))").with(listType(doubleType())).to("(sum (map log (map log (set v0))))");

      simplify("(sum (map log (sorted-set (map log (sorted-set v0)))))").with(listType(doubleType())).to("(sum (map log (set (map log (set v0)))))");
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(sum (set v0))", listType(integerType()));
      cannotSimplify("(sum (map log (set v0)))", listType(doubleType()));
   }

   @Override
   protected FunctionSet getFunctionSet() {
      return new FunctionSetBuilder().add(getFunction()).add(DoubleUtils.DOUBLE_UTILS.getSum()).add(Sort.getSingleton(), integerType())
            .add(Sort.getSingleton(), doubleType()).add(Set.getSingleton(), integerType()).add(Set.getSingleton(), doubleType())
            .add(SortedSet.getSingleton(), integerType()).add(SortedSet.getSingleton(), doubleType()).add(FILTER, integerType()).add(IS_POSITIVE)
            .add(MAP, doubleType(), integerType()).add(MAP, doubleType(), doubleType()).add(Logarithm.getSingleton(), doubleType()).build();
   }
}
