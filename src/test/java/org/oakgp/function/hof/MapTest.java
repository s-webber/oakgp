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
import static org.oakgp.type.CommonTypes.integerType;

import org.junit.Test;
import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.classify.IsNegative;
import org.oakgp.function.classify.IsPositive;
import org.oakgp.function.classify.IsZero;
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
   }

   @Override
   public void testCannotSimplify() {
   }

   @Override
   protected FunctionSet getFunctionSet() {
      return new FunctionSetBuilder().add(getFunction(), booleanType(), integerType()).add(new IsPositive()).add(new IsNegative()).add(new IsZero()).build();
   }

   @Test
   public void testIsTemplate() {
      assertTrue(new Map().getSignature().isTemplate());
   }
}
