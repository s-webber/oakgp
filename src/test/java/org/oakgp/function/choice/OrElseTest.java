package org.oakgp.function.choice;

import static org.oakgp.Type.optionalType;
import static org.oakgp.Type.stringType;

import java.util.List;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;

public class OrElseTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return new OrElse(stringType());
   }

   @Override
   protected void getEvaluateTests(EvaluateTestCases testCases) {
      ConstantNode neverNullValue = new ConstantNode("default", stringType());

      ConstantNode nullValue = new ConstantNode(null, optionalType(stringType()));
      testCases.when("(orelse v0 v1)").assigned(nullValue, neverNullValue).expect("default");

      ConstantNode nonNullValue = new ConstantNode("hello", optionalType(stringType()));
      testCases.when("(orelse v0 v1)").assigned(nonNullValue, neverNullValue).expect("hello");
   }

   @Override
   protected void getCanSimplifyTests(SimplifyTestCases testCases) {
      ConstantNode arg1 = new ConstantNode("hello", optionalType(stringType()));
      ConstantNode arg2 = new ConstantNode("world!", stringType());
      testCases.put(new FunctionNode(getFunction(), arg1, arg2), "\"hello\"");
   }

   @Override
   protected void getCannotSimplifyTests(List<String> testCases) {
      // TODO
      // allow variable types to be provided e.g.
      // testCases.add("(orelse v0 v1)", booleanType(), booleanType());
      // testCases.add("(orelse v0 v1)");
   }
}
