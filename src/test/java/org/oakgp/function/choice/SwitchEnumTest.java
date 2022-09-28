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

import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.nullableType;
import static org.oakgp.type.Types.declareType;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.node.ConstantNode;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.type.Types.Type;
import org.oakgp.util.FunctionSetBuilder;

public class SwitchEnumTest extends AbstractFunctionTest {
   private static final Type ENUM_TYPE = declareType("exampleEnum");
   private static final Type NULLABLE_ENUM_TYPE = nullableType(ENUM_TYPE);
   private static final SwitchEnum SWITCH_ENUM = new SwitchEnum(ExampleEnum.class, ENUM_TYPE, integerType());
   private static final SwitchEnum SWITCH_NULLABLE_ENUM = new SwitchEnum(ExampleEnum.class, NULLABLE_ENUM_TYPE, integerType());

   private static enum ExampleEnum {
      A, B, C
   }

   private static final ConstantNode A = new ConstantNode(ExampleEnum.A, ENUM_TYPE);
   private static final ConstantNode B = new ConstantNode(ExampleEnum.B, ENUM_TYPE);
   private static final ConstantNode C = new ConstantNode(ExampleEnum.C, ENUM_TYPE);
   private static final ConstantNode NULL = new ConstantNode(null, NULLABLE_ENUM_TYPE);

   @Override
   protected SwitchEnum getFunction() {
      return SWITCH_ENUM;
   }

   @Override
   public void testEvaluate() {
      String input = "(switch v0 9 5 6)";
      evaluate(input).assigned(A).to(9);
      evaluate(input).assigned(B).to(5);
      evaluate(input).assigned(C).to(6);

      String nullableInput = "(switch v0 9 5 6 7)";
      evaluate(nullableInput).assigned(nullable(A)).to(9);
      evaluate(nullableInput).assigned(nullable(B)).to(5);
      evaluate(nullableInput).assigned(nullable(C)).to(6);
      evaluate(nullableInput).assigned(NULL).to(7);
   }

   private ConstantNode nullable(ConstantNode n) {
      return new ConstantNode(n.evaluate(null, null), NULLABLE_ENUM_TYPE);
   }

   @Override
   public void testCanSimplify() {
      simplify("(switch v0 9 (switch v0 9 5 6) 6)").with(ENUM_TYPE).to("(switch v0 9 5 6)");
   }

   @Override
   public void testCannotSimplify() {
   }

   @Override
   protected FunctionSet getFunctionSet() {
      return new FunctionSetBuilder().add(SWITCH_ENUM).add(SWITCH_NULLABLE_ENUM).build();
   }
}
