/*
 * Copyright 2018 S. Webber
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

import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerType;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;
import org.oakgp.function.classify.IsNegative;
import org.oakgp.function.classify.IsPositive;
import org.oakgp.function.classify.IsZero;

public class GroupByTest extends AbstractFunctionTest {
   @Override
   protected GroupBy getFunction() {
      return new GroupBy(integerType(), booleanType());
   }

   @Override
   public void testEvaluate() {
      LinkedHashMap<Boolean, List<Integer>> expected = new LinkedHashMap<>();
      expected.put(true, Arrays.asList(4, 6, 8, 11, 42));
      expected.put(false, Arrays.asList(-7, -10));
      evaluate("(group-by pos? [4 6 -7 8 -10 11 42])").to(expected);

      expected = new LinkedHashMap<>();
      expected.put(false, Arrays.asList(4, 6, 8, 11, 42));
      expected.put(true, Arrays.asList(-7, -10));
      evaluate("(group-by neg? [4 6 -7 8 -10 11 42])").to(expected);

      expected = new LinkedHashMap<>();
      expected.put(false, Arrays.asList(4, 6, -7, 8, -10, 11, 42));
      evaluate("(group-by zero? [4 6 -7 8 -10 11 42])").to(expected);
   }

   @Override
   public void testCanSimplify() {
   }

   @Override
   public void testCannotSimplify() {
   }

   @Override
   protected Function[] getFunctionSet() {
      return new Function[] { getFunction(), new IsPositive(), new IsNegative(), new IsZero() };
   }
}
