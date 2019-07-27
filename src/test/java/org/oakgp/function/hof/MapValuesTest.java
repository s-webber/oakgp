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
package org.oakgp.function.hof;

import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.stringType;

import java.util.LinkedHashMap;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.classify.IsPositive;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.util.FunctionSetBuilder;

public class MapValuesTest extends AbstractFunctionTest {
   @Override
   protected MapValues getFunction() {
      return new MapValues(stringType(), integerType(), booleanType());
   }

   @Override
   public void testEvaluate() {
      LinkedHashMap<String, Boolean> expected = new LinkedHashMap<>();
      expected.put("a", true);
      expected.put("b", true);
      expected.put("c", true);
      expected.put("d", true);
      expected.put("e", false);
      expected.put("f", true);
      expected.put("g", false);
      expected.put("h", false);
      expected.put("i", true);

      evaluate("(map-values pos? {\"a\" 1 \"b\" 2 \"c\" 3 \"d\" 4 \"e\" -5 \"f\" 6 \"g\" -7 \"h\" -8 \"i\" 9})").to(expected);
   }

   @Override
   public void testCanSimplify() {
      simplify("(map-values pos? {\"a\" 1 \"b\" -2 \"c\" 3})").to("{\"a\" true \"b\" false \"c\" true}");
   }

   @Override
   public void testCannotSimplify() {
   }

   @Override
   protected FunctionSet getFunctionSet() {
      return new FunctionSetBuilder().add(getFunction()).add(new IsPositive()).build();
   }
}
