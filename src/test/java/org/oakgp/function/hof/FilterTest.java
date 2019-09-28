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
import static org.oakgp.type.CommonTypes.integerListType;

import org.oakgp.function.AbstractFunctionTest;

public class FilterTest extends AbstractFunctionTest {
   @Override
   protected Filter getFunction() {
      return new Filter();
   }

   @Override
   public void testEvaluate() {
      evaluate("(filter pos? [2 -12 8 -3 -7 6])").to(asList(2, 8, 6));
      evaluate("(filter neg? [2 -12 8 -3 -7 6])").to(asList(-12, -3, -7));
      evaluate("(filter zero? [2 -12 8 -3 -7 6])").to(asList());
   }

   @Override
   public void testCanSimplify() {
      simplify("(filter pos? [2 -12 8])").to("[2 8]");

      simplify("(filter pos? (sort v0))").with(integerListType()).to("(sort (filter pos? v0))");
      simplify("(filter pos? (set v0))").with(integerListType()).to("(set (filter pos? v0))");
      simplify("(filter pos? (sorted-set v0))").with(integerListType()).to("(sorted-set (filter pos? v0))");
      simplify("(filter pos? (sort (set v0)))").with(integerListType()).to("(sorted-set (filter pos? v0))");
      simplify("(filter pos? (set (sort v0)))").with(integerListType()).to("(sorted-set (filter pos? v0))");

      simplify("(filter zero? (filter pos? (set v0)))").with(integerListType()).to("(set (filter zero? (filter pos? v0)))");
      simplify("(filter zero? (filter pos? (sort v0)))").with(integerListType()).to("(sort (filter zero? (filter pos? v0)))");
      simplify("(filter zero? (filter pos? (sorted-set v0)))").with(integerListType()).to("(sorted-set (filter zero? (filter pos? v0)))");

      simplify("(filter pos? (filter pos? v0))").with(integerListType()).to("(filter pos? v0)");
      simplify("(filter pos? (filter zero? v0))").with(integerListType()).to("(filter zero? (filter pos? v0))");
      simplify("(filter pos? (filter zero? (filter pos? v0)))").with(integerListType()).to("(filter zero? (filter pos? v0))");
      simplify("(filter pos? (filter zero? (filter pos? (filter zero? v0))))").with(integerListType()).to("(filter zero? (filter pos? v0))");
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(filter zero? (filter pos? v0))", integerListType());
   }
}
