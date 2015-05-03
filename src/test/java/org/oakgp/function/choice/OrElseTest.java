package org.oakgp.function.choice;

import static org.oakgp.Type.optionalType;
import static org.oakgp.Type.stringType;

import java.util.List;

import org.oakgp.Type;
import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.VariableNode;

public class OrElseTest extends AbstractFunctionTest {
   private final OrElse example = new OrElse(stringType());

   @Override
   protected Function getFunction() {
      return example;
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
      // testCases.put(new FunctionNode(getFunction(), arg1, arg2), "\"hello\"");

      VariableNode v = new VariableNode(0, Type.stringType());
      FunctionNode fn = new FunctionNode(getFunction(), v, arg2);
      testCases.put(new FunctionNode(getFunction(), v, fn), fn);
   }

   @Override
   protected void getCannotSimplifyTests(List<String> testCases) {
      // TODO
      // allow variable types to be provided e.g.
      // testCases.add("(orelse v0 v1)", booleanType(), booleanType());
      // testCases.add("(orelse v0 v1)");
   }
}
