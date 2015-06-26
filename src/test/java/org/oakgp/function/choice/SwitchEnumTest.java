package org.oakgp.function.choice;

import static org.oakgp.Type.integerType;
import static org.oakgp.Type.nullableType;
import static org.oakgp.Type.type;

import org.oakgp.Type;
import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;
import org.oakgp.node.ConstantNode;

public class SwitchEnumTest extends AbstractFunctionTest {
   private static final Type ENUM_TYPE = type("exampleEnum");
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
   protected Function getFunction() {
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
      return new ConstantNode(n.evaluate(null), NULLABLE_ENUM_TYPE);
   }

   @Override
   public void testCanSimplify() {
      simplify("(switch v0 9 (switch v0 9 5 6) 6)").with(ENUM_TYPE).to("(switch v0 9 5 6)");
   }

   @Override
   public void testCannotSimplify() {
   }

   @Override
   protected Function[] getFunctionSet() {
      return new Function[] { SWITCH_ENUM, SWITCH_NULLABLE_ENUM };
   }
}
