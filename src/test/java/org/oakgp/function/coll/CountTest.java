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
package org.oakgp.function.coll;

import static org.oakgp.Type.integerType;

import org.oakgp.Arguments;
import org.oakgp.Type;
import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.node.ConstantNode;

public class CountTest extends AbstractFunctionTest {
   @Override
   protected Count getFunction() {
      return new Count(integerType());
   }

   @Override
   public void testEvaluate() {
      ConstantNode emptyList = new ConstantNode(Arguments.createArguments(), Type.arrayType(Type.integerType()));
      evaluate("(count v0)").assigned(emptyList).to(0);
      evaluate("(count [2 -12 8])").to(3);
      evaluate("(count [2 -12 8 -3 -7])").to(5);
   }

   @Override
   public void testCanSimplify() {
      simplify("(count [2 -12 8])").to("3");
   }

   @Override
   public void testCannotSimplify() {
   }
}
