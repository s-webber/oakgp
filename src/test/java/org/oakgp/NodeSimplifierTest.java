package org.oakgp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.oakgp.TestUtils.createConstant;
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.TestUtils.readNode;
import static org.oakgp.TestUtils.writeNode;

import org.junit.Test;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

public class NodeSimplifierTest {
   private final NodeSimplifier nodeSimplifier = new NodeSimplifier();

   @Test
   public void testConstantNode() {
      Node input = createConstant(1);
      Node output = nodeSimplifier.simplify(input);
      assertSame(input, output);
   }

   @Test
   public void testVariableNode() {
      Node input = createVariable(1);
      Node output = nodeSimplifier.simplify(input);
      assertSame(input, output);
   }

   @Test
   public void testFunctionNodeWithVariable() {
      Node input = readNode("(+ 7 v0)");
      Node output = nodeSimplifier.simplify(input);
      assertSame(input, output);
   }

   @Test
   public void testFunctionNodeNoVariables() {
      Node input = readNode("(+ 7 3)");
      Node output = nodeSimplifier.simplify(input);
      assertSame(ConstantNode.class, output.getClass());
      assertEquals("10", output.toString());
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToConstant() {
      Node input = readNode("(- (+ v0 3) (* 1 (- (+ v0 3) (* v2 (- v1 v1)))))");
      Node output = nodeSimplifier.simplify(input);
      assertSame(ConstantNode.class, output.getClass());
      assertEquals("0", output.toString());
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction1() {
      Node input = readNode("(+ (- 5 6) (* v0 (- (* 6 7) (+ 2 3))))");
      Node output = nodeSimplifier.simplify(input);
      assertSame(FunctionNode.class, output.getClass());
      assertEquals("(org.oakgp.operator.math.Subtract (org.oakgp.operator.math.Multiply 37 v0) 1)", output.toString());
      assertEquals(73, (int) output.evaluate(Assignments.createAssignments(2)));
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction2() {
      assertCanSimplify("(- 12 v0)", "(+ 9 (+ 3 (- 1 (+ 1 v0))))");
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction3() {
      // (- (+ v0 (+ 8 v1)) v1) = (x + (8 + y)) - y = x+8
      assertCanSimplify("(+ 8 v0)", "(- (+ v0 (+ 8 v1)) v1)");
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction4() {
      // (- (- (+ 10 (* 2 v0)) v1) v1) = ((10 + (2 * v0)) - v1) - v1 = -2y+10+2x
      assertCanSimplify("(- (+ 10 (* 2 v0)) (* 2 v1))", "(- (- (+ 10 (* 2 v0)) v1) v1)");
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction5() {
      // (+ v0 (- (+ v0 (+ 8 v2)) v1))) = x + ((x + (8 + z)) - y) = 2x+8+z-y
      Node input = readNode("(+ v0 (- (+ v0 (+ 8 v2)) v1))");
      Node output = nodeSimplifier.simplify(input);
      assertSame(FunctionNode.class, output.getClass());
      assertEquals(
            "(org.oakgp.operator.math.Subtract (org.oakgp.operator.math.Add (org.oakgp.operator.math.Multiply 2 v0) (org.oakgp.operator.math.Add 8 v2)) v1)",
            output.toString());
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction6() {
      // (+ 2 (+ (* 2 v0) (+ 8 v1))) = 2 + ((2 * x) + (8 + y)) = 10+2x+y
      assertCanSimplify("(+ (+ 10 v1) (* 2 v0))", "(+ 2 (+ (* 2 v0) (+ 8 v1)))");
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction7() {
      assertCanSimplify("(- (+ v1 (* 2 v0)) 1)", "(- (- (+ (* 2 v0) (* 2 v1)) 1) v1)");
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction8() {
      assertCanSimplify("(- (- (* 2 v0) (* 3 v1)) 1)", "(- (- (- (* 2 v0) (* 2 v1)) 1) v1)");
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction9() {
      assertCanSimplify("(- (- (* 2 v0) 9) (* 2 v1))", "(- (- (- (* 2 v0) 9) v1) v1)");
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction10() {
      assertCanSimplify("(- (+ 9 (* 2 v0)) (* 2 v1))", "(- (- (+ (* 2 v0) 9) v1) v1)");
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction11() {
      assertCanSimplify("(- (+ v0 (* 2 v1)) 1)", "(- (- (+ (* 2 v0) (* 2 v1)) 1) v0)");
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction12() {
      assertCanSimplify("(+ 1 (+ v0 (* 2 v1)))", "(- (- (+ (* 2 v0) (* 2 v1)) 1) (- v0 2))");
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction13() {
      // (- (- (+ (* 2 v1) (* 2 v0)) 1) (- v0 2)) = (((2 * y) + (2 * x)) - 1) - (x - 2) = x+1+2y
      assertCanSimplify("(+ 1 (+ v0 (* 2 v1)))", "(- (- (+ (* 2 v1) (* 2 v0)) 1) (- v0 2))");
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction14() {
      assertCanSimplify(
            "(- (+ 432 (* -108 v2)) (* 81 (* (+ (* 180 v2) (+ 819 (* 324 v0))) (- (* -3 v2) 3))))",
            "(- (+ 432 (* -108 v2)) (* 81 (* (+ -3 (* -3 v2)) (- (- (+ (* 162 v0) (* 243 v2)) (* 162 v2)) (+ (* -6 v2) (- (- (* 162 v2) (- 819 (+ (* -162 v0) (* -243 v2)))) (* 12 v2)))))))");
   }

   // (- (- (* 162 v0) (* -81 v2)) (+ (- (* -162 v0) 819) (* -99 v2))) =
   // (((162x)-(-81z))-(((-162x)-819)+(-99z))) =
   // 324x+180z+819
   @Test
   public void testDeeplyNestedTreeSimplifedToFunction15() {
      assertCanSimplify("(+ (* 180 v2) (+ 819 (* 324 v0)))", "(- (- (* 162 v0) (* -81 v2)) (+ (- (* -162 v0) 819) (* -99 v2)))");
   }

   @Test
   public void testVeryDeeplyNestedTreeSimplifedByOperator1() {
      String input = "(- v1 (- (- v0 (- v1 (- (- (+ 2 (+ v0 (- (+ v0 (+ 8 v1)) v1))) v1) v1))) 10))";
      String expected = "(- (* 4 v1) (* 3 v0))";
      assertDeeplyNestedTreeSimplifedByOperator(input, expected);
   }

   @Test
   public void testVeryDeeplyNestedTreeSimplifedByOperator2() {
      String input = "(- v1 (- (- v0 (- v1 (- (- (+ 2 (+ v0 (- (+ v0 (+ 8 v2)) v1))) v1) v1))) 10))";
      String expected = "(- (* 5 v1) (+ v2 (* 3 v0)))";
      assertDeeplyNestedTreeSimplifedByOperator(input, expected);
   }

   @Test
   public void testVeryDeeplyNestedTreeSimplifedByOperator3() {
      // TODO keep trying to improve
      String input = "(* 3 (* (* 3 (- 1 (- (- 2 (- 1 (- 1 (- 1 (+ 1 (- 1 (- (+ 2 (* 3 (* (* 3 (- 1 (- (+ 2 (- 1 (- 1 (- 1 (+ 1 (+ 1 (- (+ 2 (+ 1 (- 1 (- 1 (- 1 (- 1 (- (+ 1 (- 1 (- 1 (- (+ 2 (* 3 (* (* 3 (- 1 (- (+ 2 (- 1 (- 1 (- 1 (+ 1 (- 0 (- 1 (- (+ 2 (+ 1 (- 1 (- 1 (- 1 (- 1 (- (+ 1 (- 1 (+ 1 (- 1 (- 4 (- (+ (+ (- v3 3) (+ v2 v3)) (+ 3 (- 1 (- 1 (+ 1 v2))))) (- 1 (- 3 v2)))))))) (+ (- 1 (- 1 (+ 1 v2))) (+ -1 (- 1 (- 1 (+ 1 v2)))))))))))) (+ (- 3 (- 1 (- 1 v2))) (+ 1 (- 3 (- 1 (- 1 v2))))))))))))) 1))) -3))) (+ (- 3 (- 1 (- 1 v2))) (+ 1 (- 1 (- 1 (- 1 v2))))))))) (+ (- 1 (- 1 (+ 1 v2))) (+ 1 (- 1 (- 1 (+ 1 v2)))))))))))) (+ (- 3 (- 1 (- 1 v2))) (+ 1 (- 1 (- 1 (- (+ 2 (+ 1 (- 1 (- 1 (- 1 (- 1 (- (- 0 (+ 1 (- 1 (- 1 (- (+ 2 (* 3 (* (* 3 (- 1 (- (+ 2 (- 1 (- 1 (- 1 (+ 1 (- 0 (- 1 (- (+ 2 (+ 1 (- 1 (- 1 (- 1 (- 1 (- (+ 1 (- 1 (+ 1 (- 1 (- 4 (- (+ (+ (- v3 3) (+ v2 v3)) (+ 3 (- 1 (- 1 (+ 1 v2))))) (- 1 (- 3 v2)))))))) (+ (- 1 (- 1 (+ 1 v2))) (+ -1 (- 1 (- 1 (+ 1 v2)))))))))))) (+ (- 3 (- 1 (- 1 v2))) (+ 1 (- 3 (- 1 (- 1 v2))))))))))))) 1))) -3))) (+ (- 3 (- 1 (- 1 v2))) (+ 1 (- 1 (- 1 (- 1 v2)))))))))) (+ (- 1 (- 1 (+ 1 v2))) (+ 1 (- 1 (- 1 (+ 1 v2)))))))))))) v2)))))))))))) (+ (- 1 (- 1 (+ 1 v2))) (+ 9 (+ 1 (- 1 (+ 1 v2))))))))	(* 3 (- 0 (+ v2 1)))))) (+ (- 3 (- 1 (- 1 v2))) (+ 1 (- 1 (- 1 (- 1 v2)))))))))))) (- (- 1 (- 1 (+ 1 v2))) (+ 9 (+ 3 (- 1 (+ 1 v2)))))))) -3))";
      String expected = "(- (+ 432 (* -108 v2)) (* 81 (* (+ 819 (+ (* 180 v2) (* 324 v3))) (- (* -3 v2) 3))))";
      assertDeeplyNestedTreeSimplifedByOperator(input, expected);
   }

   @Test
   public void testVeryDeeplyNestedTreeSimplifedByOperator4() {
      // TODO keep trying to improve
      String input = "(+ (- v4 (- 3 v4)) (if (- 4 (+ v4 v2)) (+ (+ 1 (+ (- v1 (- v1 (- v1 (* v3 (* v0 v2))))) (* v4 (+ v3 v1)))) (if (- v4 (* 3 (- (if (- v3 (+ v3 (- v2 (* 3 v3)))) (+ (* (- (+ v3 (* v1 v2)) (if (- v3 (if (- (- v1 (- (- v1 4) (if (- 3 v4) v4 0))) (- (- v1 (- (- v1 1) v4)) (- v3 3))) (- v1 v4) v1)) (- (- v2 (- v3 (* v1 v2))) v4) v1)) (if (- v3 (+ 1 (+ v3 (* (+ v1 1) v2)))) 3 v1)) (if v1 v2 v1)) v4) (* 3 v2)))) 3 (+ (- 0 (- (* v1 (* v1 (- (- v1 v4) (* v3 (* v0 v2))))) (* v4 (- v3 v1)))) v1))) (+ (- v1 (- (- v1 v2) (if v4 v4 v1))) (if (* (- v3 (if (- v3 (- (if (* v3 (+ v3 (- (if (+ (- v3 (+ v1 v2)) (- (* v1 v4) (+ v4 (* (+ v1 (- v1 v4)) (if (- (* v4 (- v3 v1)) (- (- v3 (- v3 (if (- (* v1 (if (- v3 (+ v3 (- (if (* (- v3 (+ v1 v2)) (- (+ v1 v3) (* 4 (* 3 v2)))) (* (+ v1 (- (- v0 3) (* v3 v2))) (if (- (* v1 (+ v3 (+ v1 1))) (* (- v3 (- v3 (if (- (* v4 (- (- v1 1) (if (- 1 v4) v4 v1))) (- v3 (* 3 (* v1 v2)))) (- v1 v4) v1))) v2)) v2 v1)) (- (- v1 (- (- v1 1) (if (- (if (- v3 (+ v3 (- (if (+ (- v3 (+ v1 v2)) (- (+ v1 v4) (* 4 (* v1 v4)))) (- (+ v1 (- (- v1 v4) (- v3 (* v0 v2)))) (if (- (* v4 (- v3 v1)) (* (- v3 (- (+ (- v1 (- (- v1 v2) (if v4 v4 v1))) (if (* (- v3 (if (- v3 (- (if (* v3 (+ v3 (- (if (+ (- v3 (+ v1 v2)) (- (* v1 v4) (* v4 (* (+ v1 (- v1 v4)) (if (- (* v4 (- v3 v1)) (* (- v3 (- v3 (if (- (* v1 (if (- v3 (+ v3 (- (if (* (- v2 (+ v1 v2)) (- (+ v1 v3) (* 4 (* 3 v2)))) (* (+ v1 (- (- v0 3) (* v3 v2))) (if (- (* 3 (+ v3 v1)) (* (- v3 (- v3 (if (- (* v4 (- (- v1 1) (if (- 1 v4) v4 v1))) (- v3 (* 3 (* v1 v2)))) (- v1 v4) v1))) v2)) v2 v1)) (- (- v1 (- (- v1 1) (if (- (if (- v3 (+ v3 (- (if (+ (- v3 (+ v1 v2)) (- (+ v1 v4) (* 4 (* v1 v4)))) (- (+ v1 (- 0 (- v3 (* v0 v2)))) (if (- (* 2 (- v3 v1)) (* (- v3 (- v3 (if (+ (* v1 (- (- v1 1) v3)) (- v3 (* 3 (* v1 v2)))) (* v1 v4) v1))) v2)) v2 v1)) (- (- v1 (- (- v1 2) (if (- v1 v4) v4 v1))) (+ v3 3))) v3))) (* v2 (if v1 v2 v1)) v4) v4) v4 v1))) (- v3 3))) v3))) (* v2 (if v1 v4 v1)) v4)) (- v3 (* 3 (- (- 3 v4) (* 4 v2))))) (* v1 v4) v1))) v2)) v2 v4))))) (* (+ v1 (- (- v1 v2) (* v3 (* v0 v2)))) (if (- 3 (* (- v3 (- v3 (if (- (+ v1 (- (- v1 1) (if (- 1 v4) 1 v1))) (- v4 (* 3 (- (- 3 v4) (- 4 v2))))) (- v1 v4) v1))) v2)) v2 v1)) (- (- v1 (- (- v1 v3) (if (- v1 v3) 1 v1))) (- v3 3))) (* 3 v3)))) (* (if (- 1 v4) v4 0) (if v4 v2 v1)) v4) (+ v3 (* (- v1 1) 3)))) 3 v1)) (if v1 v2 v1)) (- v1 (* v1 v2)) v1)) (if (+ (* v1 (- (- v1 1) (if (- 1 v4) v4 v1))) (- v3 (* 3 (* v1 v2)))) (- v1 v4) v1))) v2)) v2 v1)) (- (- v1 (- (- v1 2) (if (- v1 v4) v4 v1))) (+ v3 3))) v3))) (* v2 (if v1 v4 v1)) v4) v4) v4 v1))) (- v3 3))) v3))) (* v2 (if v1 v4 v1)) v4)) (- v3 (* 3 (- (- 3 v4) (* 4 v2))))) (+ v1 v4) v1))) v2)) v2 v4))))) (* (* v1 (- (- v1 v2) (* v3 (* v0 v2)))) (if (- v3 (* (- v3 (- v3 (if (- (+ v1 (- (- v1 1) (if (- 1 v4) v4 v1))) (- v4 (* 3 (- (- 3 v4) (- 4 v2))))) (- v1 v4) v1))) v2)) v2 v1)) (- (- v1 (- (- v1 v3) (if (- v1 v3) 1 v1))) (- v3 3))) (* 3 v3)))) (* (if (- 1 v4) v4 0) (if v4 v2 v1)) v4) (+ v3 (* (- v1 1) 3)))) 3 v1)) (if v1 v2 v1)) (- v1 (* v1 v2)) v1))))";
      String expected = "(+ (if (- 4 (+ v2 v4)) (+ (if (- v4 (- (* 3 (if (- (* 3 v3) v2) (+ (* (if (- -1 (* v2 (+ 1 v1))) 3 v1) (- (+ v3 (* v1 v2)) (if (- v3 (if (- (if (- 3 v4) v4 0) (- v4 v3)) (- v1 v4) v1)) (- (- v2 (- v3 (* v1 v2))) v4) v1))) (if v1 v2 v1)) v4)) (* 9 v2))) 3 (+ v1 (- (* v4 (- v3 v1)) (* v1 (* v1 (- (- v1 v4) (* v3 (* v0 v2)))))))) (+ (* v4 (+ v1 v3)) (- v1 (- (* v3 (* v0 v2)) 1)))) (+ (if (* (- v3 (if (- (+ (* 2 v3) (- (* 3 v1) 3)) (if (* v3 (- (if (+ (- v3 (+ v1 v2)) (- (* v1 v4) (+ v4 (* (- (* 2 v1) v4) (if (- (* v4 (- v3 v1)) (- (if (- (* v1 (if (- v3 (if (* (- (+ v1 v3) (* 12 v2)) (- v3 (+ v1 v2))) (* (if (- (* v1 (+ v3 (+ 1 v1))) (* v2 (if (- (* v4 (- (- v1 1) (if (- 1 v4) v4 v1))) (- v3 (* 3 (* v1 v2)))) (- v1 v4) v1))) v2 v1) (+ v1 (- (- v0 3) (* v2 v3)))) (- (+ 4 (if (- (if (- v3 (if (+ (- v3 v2) (- v4 (* 4 (* v1 v4)))) (- (- (- (* 2 v1) v4) (- v3 (* v0 v2))) (if (- (* v4 (- v3 v1)) (* v2 (- v3 (- (+ (if (* (if v1 v2 v1) (- v3 (if (- (+ (* 2 v3) (- (* 3 v1) 3)) (if (* v3 (- (if (+ (- v3 (+ v1 v2)) (- (* v1 v4) (* v4 (* (if (- (* v4 (- v3 v1)) (* v2 (if (- (* v1 (if (- v3 (if (* (- 0 v1) (- (+ v1 v3) (* 12 v2))) (* (if (- (+ (* 3 v1) (* 3 v3)) (* v2 (if (- (* v4 (- (- v1 1) (if (- 1 v4) v4 v1))) (- v3 (* 3 (* v1 v2)))) (- v1 v4) v1))) v2 v1) (+ v1 (- (- v0 3) (* v2 v3)))) (- (+ 4 (if (- (if (- v3 (if (+ (- v3 v2) (- v4 (* 4 (* v1 v4)))) (- (+ v1 (- (* v0 v2) v3)) (if (- (- (* 2 v3) (* 2 v1)) (* v2 (if (+ (* v1 (- (- v1 1) v3)) (- v3 (* 3 (* v1 v2)))) (* v1 v4) v1))) v2 v1)) (- (- (if (- v1 v4) v4 v1) 1) v3))) (* v2 (if v1 v2 v1)) v4) v4) v4 v1)) v3))) (* v2 (if v1 v4 v1)) v4)) (- v3 (- (- 9 (* 3 v4)) (* 12 v2)))) (* v1 v4) v1))) v2 v4) (- (* 2 v1) v4))))) (* (if (- 3 (* v2 (if (- (- (* 2 v1) (if (- 1 v4) 1 v1)) (+ (* -3 v2) (+ 4 (* 4 v4)))) (- v1 v4) v1))) v2 v1) (- (- (* 2 v1) v2) (* v3 (* v0 v2)))) (+ 3 (if (- v1 v3) 1 v1))) (* 2 v3))) (* (if (- 1 v4) v4 0) (if v4 v2 v1)) v4)) 3 v1))) (- v1 (* v1 v2)) v1) (- (if v4 v4 v1) (- 0 v2))) (if (+ (* v1 (- (- v1 1) (if (- 1 v4) v4 v1))) (- v3 (* 3 (* v1 v2)))) (- v1 v4) v1))))) v2 v1)) (- (- (if (- v1 v4) v4 v1) 1) v3))) (* v2 (if v1 v4 v1)) v4) v4) v4 v1)) v3))) (* v2 (if v1 v4 v1)) v4)) (- v3 (- (- 9 (* 3 v4)) (* 12 v2)))) (+ v1 v4) v1) v2)) v2 v4))))) (* (* v1 (- (- v1 v2) (* v3 (* v0 v2)))) (if (- v3 (* v2 (if (- (- (* 2 v1) (if (- 1 v4) v4 v1)) (+ (* -3 v2) (+ 4 (* 4 v4)))) (- v1 v4) v1))) v2 v1)) (+ 3 (if (- v1 v3) 1 v1))) (* 2 v3))) (* (if (- 1 v4) v4 0) (if v4 v2 v1)) v4)) 3 v1)) (if v1 v2 v1)) (- v1 (* v1 v2)) v1) (- (if v4 v4 v1) (- 0 v2)))) (- (* 2 v4) 3))";
      assertDeeplyNestedTreeSimplifedByOperator(input, expected);
   }

   @Test
   public void testVeryDeeplyNestedTreeSimplifedByOperator5() {
      String input = "(- 1 (* v2 (* (* v2 (* v2 (* v2 (* (* v2 (* v2 (* (* v2 (* (* 3 (* v2 (* v2 (* v2 (* (* v2 (* v2 (* v2 (* 3 v2)))) (* (* (* v2 (* v2 (* v2 (* v2 (* v2 (* v2 (* v2 (* (* 3 v2) (* 3 (* v2 v2)))))))))) (* 3 (* v2 v2))) (* v2 v2))))))) (* v2 (* v2 (* v2 (* v2 (* v2 (* v2 (* v2 (* (* v2 (* v2 (* v2 (* v2 (* v2 (* (* 3 v2) (* 3 (* v2 v2)))))))) (* 3 v2))))))))))) (* v2 (* v2 (* v2 (* v2 (* v2 (* (* v2 (* 3 (* v2 (* v2 (* (* v2 (* v2 (* v2 (* v2 (* v2 (* v2 (* v2 (* (* (* v2 (* v2 (* v2 (* (* 3 v2) (* 3 (- v2 v2)))))) (* v2 v2)) (* 3 v2))))))))) (* 3 (* v2 v2))))))) (* 3 v2)))))))))) (* (* v2 v2) (* v2 v2)))))) (* v2 v2))))";
      String expected = "1";
      assertDeeplyNestedTreeSimplifedByOperator(input, expected);
   }

   private void assertDeeplyNestedTreeSimplifedByOperator(String before, String expectedOutput) { // TODO call assertCanSimplify directly instead
      assertCanSimplify(expectedOutput, before);
   }

   private void assertCanSimplify(String expected, String input) { // TODO switch argument order? input/expected
      Node inputNode = readNode(input);
      Node simplifiedVersion = nodeSimplifier.simplify(inputNode);

      // test simplified version produces the same results as original (i.e. unsimplified) version
      Object[][] assignedValues = { { 0, 0, 0, 0, 0 }, { 1, 21, 8, -3, 3 }, { 2, 14, 4, 5, 6 }, { 3, -6, 2, 12, 4 }, { 7, 3, -1, 0, -6 }, { -1, 9, 7, 4, 0 },
            { -7, 0, -2, -3, 8 } };
      String simplifiedVersionString = writeNode(simplifiedVersion);
      for (Object[] assignedValue : assignedValues) {
         Assignments assignments = Assignments.createAssignments(assignedValue);
         assertEquals(simplifiedVersionString, evaluate(inputNode, assignments), evaluate(simplifiedVersion, assignments));
      }

      // test actual simplified version matches expected
      Node expectedNode = readNode(expected);
      String expectedVersionString = writeNode(expectedNode);
      assertEquals(expectedVersionString.length() + " vs. " + simplifiedVersionString.length(), expectedVersionString, simplifiedVersionString);
      assertEquals(simplifiedVersionString, expectedNode.toString(), simplifiedVersion.toString());
      assertEquals(expectedNode, simplifiedVersion);
   }

   private Object evaluate(Node n, Assignments assignments) {
      return n.evaluate(assignments);
   }
}
