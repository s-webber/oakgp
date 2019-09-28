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

import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.listType;

import java.util.Collections;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.node.ConstantNode;

public class IsNoneTest extends AbstractFunctionTest {
   @Override
   protected IsNone getFunction() {
      return new IsNone();
   }

   @Override
   public void testEvaluate() {
      ConstantNode emptyList = new ConstantNode(Collections.emptyList(), listType(integerType()));
      evaluate("(none? zero? v0)").assigned(emptyList).to(true);
      evaluate("(none? zero? [0])").to(false);
      evaluate("(none? zero? [1])").to(true);
      evaluate("(none? zero? [0 0 0])").to(false);
      evaluate("(none? zero? [1 0 0])").to(false);
      evaluate("(none? zero? [0 1 0])").to(false);
      evaluate("(none? zero? [0 0 1])").to(false);
      evaluate("(none? zero? [1 1 0])").to(false);
      evaluate("(none? zero? [1 0 1])").to(false);
      evaluate("(none? zero? [0 1 1])").to(false);
      evaluate("(none? zero? [1 1 1])").to(true);
   }

   @Override
   public void testCanSimplify() {
      simplify("(none? zero? [0])").to("false");
      simplify("(none? zero? [1])").to("true");
   }

   @Override
   public void testCannotSimplify() {
   }
}
