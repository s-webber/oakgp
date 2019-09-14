/*
 * Copyright 2015 S. Webber
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
package org.oakgp.function.hof;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.doubleType;
import static org.oakgp.type.CommonTypes.integerListType;
import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.listType;

import org.junit.Test;
import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.classify.IsFalse;
import org.oakgp.function.classify.IsNegative;
import org.oakgp.function.classify.IsPositive;
import org.oakgp.function.classify.IsZero;
import org.oakgp.function.coll.Set;
import org.oakgp.function.coll.Sort;
import org.oakgp.function.coll.SortedSet;
import org.oakgp.function.math.Logarithm;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.util.FunctionSetBuilder;

public class MapTest extends AbstractFunctionTest {
   @Override
   protected Map getFunction() {
      return new Map();
   }

   @Override
   public void testEvaluate() {
      evaluate("(map pos? [2 -12 8 -3 -7 6])").to(asList(true, false, true, false, false, true));
      evaluate("(map neg? [2 -12 8 -3 -7 6])").to(asList(false, true, false, true, true, false));
      evaluate("(map zero? [2 -12 8 -3 -7 6])").to(asList(false, false, false, false, false, false));
   }

   @Override
   public void testCanSimplify() {
      simplify("(map pos? [2 -12 8])").to("[true false true]");

      simplify("(map log (sort (map log (sort v0))))").with(listType(doubleType())).to("(map log (sort (map log v0)))");
      simplify("(map log (sort (map log (sorted-set v0))))").with(listType(doubleType())).to("(map log (sort (map log (set v0))))");
      simplify("(map false? (sorted-set (map pos? (sorted-set v0))))").with(integerListType()).to("(map false? (sorted-set (map pos? (set v0))))");
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(map pos? (set v0))", integerListType());
      cannotSimplify("(map false? (map pos? (set v0)))", integerListType());
      cannotSimplify("(map false? (set (map pos? (set v0))))", integerListType());
      cannotSimplify("(map pos? (sorted-set v0))", integerListType());
      cannotSimplify("(map false? (set (map pos? (sorted-set v0))))", integerListType());
      cannotSimplify("(map false? (sorted-set (map pos? (set v0))))", integerListType());
   }

   @Override
   protected FunctionSet getFunctionSet() {
      return new FunctionSetBuilder().add(getFunction(), booleanType(), integerType()).add(getFunction(), booleanType(), booleanType()).add(new IsFalse())
            .add(getFunction(), doubleType(), doubleType()).add(new IsFalse()).add(new IsPositive()).add(new IsNegative()).add(new IsZero())
            .add(Sort.getSingleton(), integerType()).add(Sort.getSingleton(), booleanType()).add(Set.getSingleton(), integerType())
            .add(Set.getSingleton(), booleanType()).add(SortedSet.getSingleton(), booleanType()).add(SortedSet.getSingleton(), integerType())
            .add(new Logarithm(), doubleType()).add(Sort.getSingleton(), doubleType()).add(Set.getSingleton(), doubleType())
            .add(SortedSet.getSingleton(), doubleType()).add(Set.getSingleton(), integerType()).build();
   }

   @Test
   public void testIsTemplate() {
      assertTrue(new Map().getSignature().isTemplate());
   }
}
