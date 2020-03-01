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
package org.oakgp.function.classify;

import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.integerType;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.node.ConstantNode;

public class IsFalseTest extends AbstractFunctionTest {
   @Override
   protected IsFalse getFunction() {
      return IsFalse.getSingleton();
   }

   @Override
   public void testEvaluate() {
      evaluate("(false? true)").to(false);
      evaluate("(false? false)").to(true);
      evaluate("(false? v0)").assigned(new ConstantNode(true, booleanType())).to(false);
      evaluate("(false? v0)").assigned(new ConstantNode(false, booleanType())).to(true);
      evaluate("(false? (zero? v0))").assigned(new ConstantNode(0, integerType())).to(false);
      evaluate("(false? (zero? v0))").assigned(new ConstantNode(1, integerType())).to(true);
   }

   @Override
   public void testCanSimplify() {
      simplify("(false? (false? true))").to("true");
      simplify("(false? (false? false))").to("false");
      simplify("(false? (false? (false? true)))").to("false");
      simplify("(false? (false? (false? false)))").to("true");
      simplify("(false? (false? (zero? v0)))").with(integerType()).to("(zero? v0)");
      simplify("(false? (false? (false? (zero? v0))))").with(integerType()).to("(false? (zero? v0))");

      simplify("(false? (or v0 v1))").with(booleanType(), booleanType()).to("(and (false? v0) (false? v1))");
      simplify("(false? (or v0 (or v1 v2)))").with(booleanType(), booleanType(), booleanType()).to("(and (false? v0) (and (false? v2) (false? v1)))");

      simplify("(false? (odd? v0))").to("(even? v0)");
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(false? v0)", booleanType());
      cannotSimplify("(false? (zero? v0))", integerType());
   }

   @Override
   protected BooleanFunctionExpectationsBuilder createBooleanFunctionExpectationsBuilder() {
      return new BooleanFunctionExpectationsBuilder("(false? v0)", booleanType()) //
            .opposite("v0");
   }
}
