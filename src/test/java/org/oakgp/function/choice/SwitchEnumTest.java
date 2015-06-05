package org.oakgp.function.choice;

import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.Type.integerType;
import static org.oakgp.Type.type;

import java.util.List;

import org.oakgp.Type;
import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.VariableNode;

public class SwitchEnumTest extends AbstractFunctionTest {
   private static final Type ENUM_TYPE = type("exampleEnum");
   private static final SwitchEnum SWITCH_ENUM = new SwitchEnum(ExampleEnum.class, ENUM_TYPE, integerType());

   private static enum ExampleEnum {
      A, B, C
   }

   private static final ConstantNode A = new ConstantNode(ExampleEnum.A, ENUM_TYPE);
   private static final ConstantNode B = new ConstantNode(ExampleEnum.B, ENUM_TYPE);
   private static final ConstantNode C = new ConstantNode(ExampleEnum.C, ENUM_TYPE);

   @Override
   protected Function getFunction() {
      return SWITCH_ENUM;
   }

   @Override
   protected void getEvaluateTests(EvaluateTestCases testCases) {
      testCases.when("(switchenum v0 9 5 6)").assigned(A).expect(9);
      testCases.when("(switchenum v0 9 5 6)").assigned(B).expect(5);
      testCases.when("(switchenum v0 9 5 6)").assigned(C).expect(6);
   }

   @Override
   protected void getCanSimplifyTests(SimplifyTestCases testCases) {
      VariableNode var = new VariableNode(0, ENUM_TYPE);
      FunctionNode arg = new FunctionNode(getFunction(), var, integerConstant(9), integerConstant(5), integerConstant(6));
      FunctionNode f = new FunctionNode(getFunction(), var, integerConstant(9), arg, integerConstant(6));
      testCases.put(f, arg);
   }

   @Override
   protected void getCannotSimplifyTests(List<String> testCases) {
   }
}
