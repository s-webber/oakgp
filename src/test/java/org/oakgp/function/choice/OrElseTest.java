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
package org.oakgp.function.choice;

import static org.junit.Assert.assertEquals;
import static org.oakgp.type.CommonTypes.nullableType;
import static org.oakgp.type.CommonTypes.stringType;

import org.oakgp.NodeSimplifier;
import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;

public class OrElseTest extends AbstractFunctionTest {
   private static final OrElse EXAMPLE = new OrElse(stringType());

   @Override
   protected OrElse getFunction() {
      return EXAMPLE;
   }

   @Override
   public void testEvaluate() {
      ConstantNode neverNullValue = new ConstantNode("default", stringType());

      ConstantNode nullValue = new ConstantNode(null, nullableType(stringType()));
      evaluate("(or-else v0 v1)").assigned(nullValue, neverNullValue).to("default");

      ConstantNode nonNullValue = new ConstantNode("hello", nullableType(stringType()));
      evaluate("(or-else v0 v1)").assigned(nonNullValue, neverNullValue).to("hello");
   }

   @Override
   public void testCanSimplify() {
      ConstantNode arg1 = new ConstantNode("hello", nullableType(stringType()));
      ConstantNode arg2 = new ConstantNode("world!", stringType());
      simplify(new FunctionNode(getFunction(), stringType(), arg1, arg2), new ConstantNode("hello", stringType()));

      VariableNode v0 = new VariableNode(0, stringType());
      FunctionNode fn = new FunctionNode(getFunction(), stringType(), v0, arg2);
      simplify(new FunctionNode(getFunction(), stringType(), v0, fn), fn);

      simplify(new FunctionNode(getFunction(), stringType(), v0,
            new FunctionNode(getFunction(), stringType(), v0, new FunctionNode(getFunction(), stringType(), v0, fn))), fn);

      VariableNode v1 = new VariableNode(1, stringType());
      simplify(
            new FunctionNode(getFunction(), stringType(), v0,
                  new FunctionNode(getFunction(), stringType(), v1, new FunctionNode(getFunction(), stringType(), v0, fn))),
            new FunctionNode(getFunction(), stringType(), v0, new FunctionNode(getFunction(), stringType(), v1, arg2)));
   }

   private void simplify(FunctionNode input, Node expected) {
      assertEquals(expected, NodeSimplifier.simplify(input));
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(or-else v0 v1)", nullableType(stringType()), stringType());
   }
}
