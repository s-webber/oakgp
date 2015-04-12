package org.oakgp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.TestUtils.readNode;
import static org.oakgp.TestUtils.writeNode;

import org.junit.ComparisonFailure;
import org.junit.Test;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

public class NodeSimplifierTest {
   @Test
   public void testConstantNode() {
      Node input = integerConstant(1);
      Node output = NodeSimplifier.simplify(input);
      assertSame(input, output);
   }

   @Test
   public void testVariableNode() {
      Node input = createVariable(1);
      Node output = NodeSimplifier.simplify(input);
      assertSame(input, output);
   }

   @Test
   public void testFunctionNodeWithVariable() {
      Node input = readNode("(+ 7 v0)");
      Node output = NodeSimplifier.simplify(input);
      assertSame(input, output);
   }

   @Test
   public void testFunctionNodeNoVariables() {
      Node input = readNode("(+ 7 3)");
      Node output = NodeSimplifier.simplify(input);
      assertSame(ConstantNode.class, output.getClass());
      assertEquals("10", output.toString());
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToConstant() {
      Node input = readNode("(- (+ v0 3) (* 1 (- (+ v0 3) (* v2 (- v1 v1)))))");
      Node output = NodeSimplifier.simplify(input);
      assertSame(ConstantNode.class, output.getClass());
      assertEquals("0", output.toString());
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction1() {
      Node input = readNode("(+ (- 5 6) (* v0 (- (* 6 7) (+ 2 3))))");
      Node output = NodeSimplifier.simplify(input);
      assertSame(FunctionNode.class, output.getClass());
      assertEquals("(org.oakgp.function.math.Subtract (org.oakgp.function.math.Multiply 37 v0) 1)", output.toString());
      assertEquals(73, (int) output.evaluate(Assignments.createAssignments(2)));
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction2() {
      when("(+ 9 (+ 3 (- 1 (+ 1 v0))))").expect("(- 12 v0)");
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction3() {
      // (- (+ v0 (+ 8 v1)) v1) = (x + (8 + y)) - y = x+8
      when("(- (+ v0 (+ 8 v1)) v1)").expect("(+ 8 v0)");
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction4() {
      // (- (- (+ 10 (* 2 v0)) v1) v1) = ((10 + (2 * v0)) - v1) - v1 = -2y+10+2x
      when("(- (- (+ 10 (* 2 v0)) v1) v1)").expect("(- (+ 10 (* 2 v0)) (* 2 v1))");
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction5() {
      // (+ v0 (- (+ v0 (+ 8 v2)) v1))) = x + ((x + (8 + z)) - y) = 2x+8+z-y
      Node input = readNode("(+ v0 (- (+ v0 (+ 8 v2)) v1))");
      Node output = NodeSimplifier.simplify(input);
      assertSame(FunctionNode.class, output.getClass());
      assertEquals(
            "(org.oakgp.function.math.Subtract (org.oakgp.function.math.Add (org.oakgp.function.math.Multiply 2 v0) (org.oakgp.function.math.Add 8 v2)) v1)",
            output.toString());
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction6() {
      // (+ 2 (+ (* 2 v0) (+ 8 v1))) = 2 + ((2 * x) + (8 + y)) = 10+2x+y
      when("(+ 2 (+ (* 2 v0) (+ 8 v1)))").expect("(+ (* 2 v0) (+ 10 v1))");
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction7() {
      when("(- (- (+ (* 2 v0) (* 2 v1)) 1) v1)").expect("(- (+ v1 (* 2 v0)) 1)");
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction8() {
      when("(- (- (- (* 2 v0) (* 2 v1)) 1) v1)").expect("(- (- (* 2 v0) (* 3 v1)) 1)");
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction9() {
      when("(- (- (- (* 2 v0) 9) v1) v1)").expect("(- (- (* 2 v0) 9) (* 2 v1))");
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction10() {
      when("(- (- (+ (* 2 v0) 9) v1) v1)").expect("(- (+ 9 (* 2 v0)) (* 2 v1))");
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction11() {
      when("(- (- (+ (* 2 v0) (* 2 v1)) 1) v0)").expect("(- (+ v0 (* 2 v1)) 1)");
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction12() {
      when("(- (- (+ (* 2 v0) (* 2 v1)) 1) (- v0 2))").expect("(+ 1 (+ v0 (* 2 v1)))");
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction13() {
      // (- (- (+ (* 2 v1) (* 2 v0)) 1) (- v0 2)) = (((2 * y) + (2 * x)) - 1) - (x - 2) = x+1+2y
      when("(- (- (+ (* 2 v1) (* 2 v0)) 1) (- v0 2))").expect("(+ 1 (+ v0 (* 2 v1)))");
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction14() {
      when(
            "(- (+ 432 (* -108 v2)) (* 81 (* (+ -3 (* -3 v2)) (- (- (+ (* 162 v0) (* 243 v2)) (* 162 v2)) (+ (* -6 v2) (- (- (* 162 v2) (- 819 (+ (* -162 v0) (* -243 v2)))) (* 12 v2)))))))")
            .expect("(- (+ 432 (* -108 v2)) (* 81 (* (+ (+ 819 (* 324 v0)) (* 180 v2)) (- (* -3 v2) 3))))");
   }

   @Test
   public void testDeeplyNestedTreeSimplifedToFunction15() {
      // (- (- (* 162 v0) (* -81 v2)) (+ (- (* -162 v0) 819) (* -99 v2))) =
      // (((162x)-(-81z))-(((-162x)-819)+(-99z))) =
      // 324x+180z+819
      when("(- (- (* 162 v0) (* -81 v2)) (+ (- (* -162 v0) 819) (* -99 v2)))").expect("(+ (+ 819 (* 324 v0)) (* 180 v2))");
   }

   @Test
   public void testVeryDeeplyNestedTreeSimplifedByFunction1() {
      String input = "(- v1 (- (- v0 (- v1 (- (- (+ 2 (+ v0 (- (+ v0 (+ 8 v1)) v1))) v1) v1))) 10))";
      String expected = "(- (* 4 v1) (* 3 v0))";
      when(input).expect(expected);
   }

   @Test
   public void testVeryDeeplyNestedTreeSimplifedByFunction2() {
      String input = "(- v1 (- (- v0 (- v1 (- (- (+ 2 (+ v0 (- (+ v0 (+ 8 v2)) v1))) v1) v1))) 10))";
      String expected = "(- (* 5 v1) (+ v2 (* 3 v0)))";
      when(input).expect(expected);
   }

   @Test
   public void testVeryDeeplyNestedTreeSimplifedByFunction3() {
      // TODO keep trying to improve
      String input = "(* 3 (* (* 3 (- 1 (- (- 2 (- 1 (- 1 (- 1 (+ 1 (- 1 (- (+ 2 (* 3 (* (* 3 (- 1 (- (+ 2 (- 1 (- 1 (- 1 (+ 1 (+ 1 (- (+ 2 (+ 1 (- 1 (- 1 (- 1 (- 1 (- (+ 1 (- 1 (- 1 (- (+ 2 (* 3 (* (* 3 (- 1 (- (+ 2 (- 1 (- 1 (- 1 (+ 1 (- 0 (- 1 (- (+ 2 (+ 1 (- 1 (- 1 (- 1 (- 1 (- (+ 1 (- 1 (+ 1 (- 1 (- 4 (- (+ (+ (- v3 3) (+ v2 v3)) (+ 3 (- 1 (- 1 (+ 1 v2))))) (- 1 (- 3 v2)))))))) (+ (- 1 (- 1 (+ 1 v2))) (+ -1 (- 1 (- 1 (+ 1 v2)))))))))))) (+ (- 3 (- 1 (- 1 v2))) (+ 1 (- 3 (- 1 (- 1 v2))))))))))))) 1))) -3))) (+ (- 3 (- 1 (- 1 v2))) (+ 1 (- 1 (- 1 (- 1 v2))))))))) (+ (- 1 (- 1 (+ 1 v2))) (+ 1 (- 1 (- 1 (+ 1 v2)))))))))))) (+ (- 3 (- 1 (- 1 v2))) (+ 1 (- 1 (- 1 (- (+ 2 (+ 1 (- 1 (- 1 (- 1 (- 1 (- (- 0 (+ 1 (- 1 (- 1 (- (+ 2 (* 3 (* (* 3 (- 1 (- (+ 2 (- 1 (- 1 (- 1 (+ 1 (- 0 (- 1 (- (+ 2 (+ 1 (- 1 (- 1 (- 1 (- 1 (- (+ 1 (- 1 (+ 1 (- 1 (- 4 (- (+ (+ (- v3 3) (+ v2 v3)) (+ 3 (- 1 (- 1 (+ 1 v2))))) (- 1 (- 3 v2)))))))) (+ (- 1 (- 1 (+ 1 v2))) (+ -1 (- 1 (- 1 (+ 1 v2)))))))))))) (+ (- 3 (- 1 (- 1 v2))) (+ 1 (- 3 (- 1 (- 1 v2))))))))))))) 1))) -3))) (+ (- 3 (- 1 (- 1 v2))) (+ 1 (- 1 (- 1 (- 1 v2)))))))))) (+ (- 1 (- 1 (+ 1 v2))) (+ 1 (- 1 (- 1 (+ 1 v2)))))))))))) v2)))))))))))) (+ (- 1 (- 1 (+ 1 v2))) (+ 9 (+ 1 (- 1 (+ 1 v2))))))))	(* 3 (- 0 (+ v2 1)))))) (+ (- 3 (- 1 (- 1 v2))) (+ 1 (- 1 (- 1 (- 1 v2)))))))))))) (- (- 1 (- 1 (+ 1 v2))) (+ 9 (+ 3 (- 1 (+ 1 v2)))))))) -3))";
      String expected = "(- (+ 432 (* -108 v2)) (* 81 (* (- (* -3 v2) 3) (+ 819 (+ (* 324 v3) (* 180 v2))))))";
      when(input).expect(expected);
   }

   @Test
   public void testVeryDeeplyNestedTreeSimplifedByFunction4() {
      // TODO keep trying to improve
      String input = "(+ (- v4 (- 3 v4)) (if (< 0 (- 4 (+ v4 v2))) (+ (+ 1 (+ (- v1 (- v1 (- v1 (* v3 (* v0 v2))))) (* v4 (+ v3 v1)))) (if (< 0 (- v4 (* 3 (- (if (< 0 (- v3 (+ v3 (- v2 (* 3 v3))))) (+ (* (- (+ v3 (* v1 v2)) (if (< 0 (- v3 (if (< 0 (- (- v1 (- (- v1 4) (if (< 0 (- 3 v4)) v4 0))) (- (- v1 (- (- v1 1) v4)) (- v3 3)))) (- v1 v4) v1))) (- (- v2 (- v3 (* v1 v2))) v4) v1)) (if (< 0 (- v3 (+ 1 (+ v3 (* (+ v1 1) v2))))) 3 v1)) (if (< 0 v1) v2 v1)) v4) (* 3 v2))))) 3 (+ (- 0 (- (* v1 (* v1 (- (- v1 v4) (* v3 (* v0 v2))))) (* v4 (- v3 v1)))) v1))) (+ (- v1 (- (- v1 v2) (if (< 0 v4) v4 v1))) (if (< 0 (* (- v3 (if (< 0 (- v3 (- (if (< 0 (* v3 (+ v3 (- (if (< 0 (+ (- v3 (+ v1 v2)) (- (* v1 v4) (+ v4 (* (+ v1 (- v1 v4)) (if (< 0 (- (* v4 (- v3 v1)) (- (- v3 (- v3 (if (< 0 (- (* v1 (if (< 0 (- v3 (+ v3 (- (if (< 0 (* (- v3 (+ v1 v2)) (- (+ v1 v3) (* 4 (* 3 v2))))) (* (+ v1 (- (- v0 3) (* v3 v2))) (if (< 0 (- (* v1 (+ v3 (+ v1 1))) (* (- v3 (- v3 (if (< 0 (- (* v4 (- (- v1 1) (if (< 0 (- 1 v4)) v4 v1))) (- v3 (* 3 (* v1 v2))))) (- v1 v4) v1))) v2))) v2 v1)) (- (- v1 (- (- v1 1) (if (< 0 (- (if (< 0 (- v3 (+ v3 (- (if (< 0 (+ (- v3 (+ v1 v2)) (- (+ v1 v4) (* 4 (* v1 v4))))) (- (+ v1 (- (- v1 v4) (- v3 (* v0 v2)))) (if (< 0 (- (* v4 (- v3 v1)) (* (- v3 (- (+ (- v1 (- (- v1 v2) (if (< 0 v4) v4 v1))) (if (< 0 (* (- v3 (if (< 0 (- v3 (- (if (< 0 (* v3 (+ v3 (- (if (< 0 (+ (- v3 (+ v1 v2)) (- (* v1 v4) (* v4 (* (+ v1 (- v1 v4)) (if (< 0 (- (* v4 (- v3 v1)) (* (- v3 (- v3 (if (< 0 (- (* v1 (if (< 0 (- v3 (+ v3 (- (if (< 0 (* (- v2 (+ v1 v2)) (- (+ v1 v3) (* 4 (* 3 v2))))) (* (+ v1 (- (- v0 3) (* v3 v2))) (if (< 0 (- (* 3 (+ v3 v1)) (* (- v3 (- v3 (if (< 0 (- (* v4 (- (- v1 1) (if (< 0 (- 1 v4)) v4 v1))) (- v3 (* 3 (* v1 v2))))) (- v1 v4) v1))) v2))) v2 v1)) (- (- v1 (- (- v1 1) (if (< 0 (- (if (< 0 (- v3 (+ v3 (- (if (< 0 (+ (- v3 (+ v1 v2)) (- (+ v1 v4) (* 4 (* v1 v4))))) (- (+ v1 (- 0 (- v3 (* v0 v2)))) (if (< 0 (- (* 2 (- v3 v1)) (* (- v3 (- v3 (if (< 0 (+ (* v1 (- (- v1 1) v3)) (- v3 (* 3 (* v1 v2))))) (* v1 v4) v1))) v2))) v2 v1)) (- (- v1 (- (- v1 2) (if (< 0 (- v1 v4)) v4 v1))) (+ v3 3))) v3)))) (* v2 (if (< 0 v1) v2 v1)) v4) v4)) v4 v1))) (- v3 3))) v3)))) (* v2 (if (< 0 v1) v4 v1)) v4)) (- v3 (* 3 (- (- 3 v4) (* 4 v2)))))) (* v1 v4) v1))) v2))) v2 v4)))))) (* (+ v1 (- (- v1 v2) (* v3 (* v0 v2)))) (if (< 0 (- 3 (* (- v3 (- v3 (if (< 0 (- (+ v1 (- (- v1 1) (if (< 0 (- 1 v4)) 1 v1))) (- v4 (* 3 (- (- 3 v4) (- 4 v2)))))) (- v1 v4) v1))) v2))) v2 v1)) (- (- v1 (- (- v1 v3) (if (< 0 (- v1 v3)) 1 v1))) (- v3 3))) (* 3 v3))))) (* (if (< 0 (- 1 v4)) v4 0) (if (< 0 v4) v2 v1)) v4) (+ v3 (* (- v1 1) 3))))) 3 v1)) (if (< 0 v1) v2 v1))) (- v1 (* v1 v2)) v1)) (if (< 0 (+ (* v1 (- (- v1 1) (if (< 0 (- 1 v4)) v4 v1))) (- v3 (* 3 (* v1 v2))))) (- v1 v4) v1))) v2))) v2 v1)) (- (- v1 (- (- v1 2) (if (< 0 (- v1 v4)) v4 v1))) (+ v3 3))) v3)))) (* v2 (if (< 0 v1) v4 v1)) v4) v4)) v4 v1))) (- v3 3))) v3)))) (* v2 (if (< 0 v1) v4 v1)) v4)) (- v3 (* 3 (- (- 3 v4) (* 4 v2)))))) (+ v1 v4) v1))) v2))) v2 v4)))))) (* (* v1 (- (- v1 v2) (* v3 (* v0 v2)))) (if (< 0 (- v3 (* (- v3 (- v3 (if (< 0 (- (+ v1 (- (- v1 1) (if (< 0 (- 1 v4)) v4 v1))) (- v4 (* 3 (- (- 3 v4) (- 4 v2)))))) (- v1 v4) v1))) v2))) v2 v1)) (- (- v1 (- (- v1 v3) (if (< 0 (- v1 v3)) 1 v1))) (- v3 3))) (* 3 v3))))) (* (if (< 0 (- 1 v4)) v4 0) (if (< 0 v4) v2 v1)) v4) (+ v3 (* (- v1 1) 3))))) 3 v1)) (if (< 0 v1) v2 v1))) (- v1 (* v1 v2)) v1))))";
      String expected = "(+ (- (* 2 v4) 3) (if (< 0 (- 4 (+ v2 v4))) (+ (+ (* v4 (+ v1 v3)) (- v1 (- (* v3 (* v0 v2)) 1))) (if (< 0 (- v4 (- (* 3 (if (< 0 (- (* 3 v3) v2)) (+ (if (< 0 v1) v2 v1) (* (if (< 0 (- -1 (* v2 (+ 1 v1)))) 3 v1) (- (+ v3 (* v1 v2)) (if (< 0 (- v3 (if (< 0 (- (if (< 0 (- 3 v4)) v4 0) (- v4 v3))) (- v1 v4) v1))) (- (- v2 (- v3 (* v1 v2))) v4) v1)))) v4)) (* 9 v2)))) 3 (+ v1 (- (* v4 (- v3 v1)) (* v1 (* v1 (- (- v1 v4) (* v3 (* v0 v2))))))))) (+ (if (< 0 (* (if (< 0 v1) v2 v1) (- v3 (if (< 0 (- (+ (- (* 3 v1) 3) (* 2 v3)) (if (< 0 (* v3 (- (if (< 0 (+ (- (* v1 v4) (+ v4 (* (- (* 2 v1) v4) (if (< 0 (- (* v4 (- v3 v1)) (- (if (< 0 (- (* v1 (if (< 0 (- v3 (if (< 0 (* (- (+ v1 v3) (* 12 v2)) (- v3 (+ v1 v2)))) (* (if (< 0 (- (* v1 (+ v3 (+ 1 v1))) (* v2 (if (< 0 (- (* v4 (- (- v1 1) (if (< 0 (- 1 v4)) v4 v1))) (- v3 (* 3 (* v1 v2))))) (- v1 v4) v1)))) v2 v1) (+ v1 (- (- v0 3) (* v2 v3)))) (- (+ 4 (if (< 0 (- (if (< 0 (- v3 (if (< 0 (+ (- v4 (* 4 (* v1 v4))) (- v3 v2))) (- (- (- (* 2 v1) v4) (- v3 (* v0 v2))) (if (< 0 (- (* v4 (- v3 v1)) (* v2 (- v3 (- (+ (if (< 0 (* (if (< 0 v1) v2 v1) (- v3 (if (< 0 (- (+ (- (* 3 v1) 3) (* 2 v3)) (if (< 0 (* v3 (- (if (< 0 (+ (- (* v1 v4) (* v4 (* (- (* 2 v1) v4) (if (< 0 (- (* v4 (- v3 v1)) (* v2 (if (< 0 (- (* v1 (if (< 0 (- v3 (if (< 0 (* (- (+ v1 v3) (* 12 v2)) (- 0 v1))) (* (if (< 0 (- (+ (* 3 v1) (* 3 v3)) (* v2 (if (< 0 (- (* v4 (- (- v1 1) (if (< 0 (- 1 v4)) v4 v1))) (- v3 (* 3 (* v1 v2))))) (- v1 v4) v1)))) v2 v1) (+ v1 (- (- v0 3) (* v2 v3)))) (- (+ 4 (if (< 0 (- (if (< 0 (- v3 (if (< 0 (+ (- v4 (* 4 (* v1 v4))) (- v3 v2))) (- (+ v1 (- (* v0 v2) v3)) (if (< 0 (- (- (* 2 v3) (* 2 v1)) (* v2 (if (< 0 (+ (- v3 (* 3 (* v1 v2))) (* v1 (- (- v1 1) v3)))) (* v1 v4) v1)))) v2 v1)) (- (- (if (< 0 (- v1 v4)) v4 v1) 1) v3)))) (* v2 (if (< 0 v1) v2 v1)) v4) v4)) v4 v1)) v3)))) (* v2 (if (< 0 v1) v4 v1)) v4)) (- v3 (- (- 9 (* 3 v4)) (* 12 v2))))) (* v1 v4) v1)))) v2 v4)))) (- v3 (+ v1 v2)))) (* (if (< 0 (- 3 (* v2 (if (< 0 (- (- (* 2 v1) (if (< 0 (- 1 v4)) 1 v1)) (+ (+ 4 (* 4 v4)) (* -3 v2)))) (- v1 v4) v1)))) v2 v1) (- (- (* 2 v1) v2) (* v3 (* v0 v2)))) (+ 3 (if (< 0 (- v1 v3)) 1 v1))) (* 2 v3)))) (* (if (< 0 v4) v2 v1) (if (< 0 (- 1 v4)) v4 0)) v4))) 3 v1)))) (- v1 (* v1 v2)) v1) (- (if (< 0 v4) v4 v1) (- 0 v2))) (if (< 0 (+ (- v3 (* 3 (* v1 v2))) (* v1 (- (- v1 1) (if (< 0 (- 1 v4)) v4 v1))))) (- v1 v4) v1)))))) v2 v1)) (- (- (if (< 0 (- v1 v4)) v4 v1) 1) v3)))) (* v2 (if (< 0 v1) v4 v1)) v4) v4)) v4 v1)) v3)))) (* v2 (if (< 0 v1) v4 v1)) v4)) (- v3 (- (- 9 (* 3 v4)) (* 12 v2))))) (+ v1 v4) v1) v2))) v2 v4)))) (- v3 (+ v1 v2)))) (* (if (< 0 (- v3 (* v2 (if (< 0 (- (- (* 2 v1) (if (< 0 (- 1 v4)) v4 v1)) (+ (+ 4 (* 4 v4)) (* -3 v2)))) (- v1 v4) v1)))) v2 v1) (* v1 (- (- v1 v2) (* v3 (* v0 v2))))) (+ 3 (if (< 0 (- v1 v3)) 1 v1))) (* 2 v3)))) (* (if (< 0 v4) v2 v1) (if (< 0 (- 1 v4)) v4 0)) v4))) 3 v1)))) (- v1 (* v1 v2)) v1) (- (if (< 0 v4) v4 v1) (- 0 v2)))))";
      when(input).expect(expected);
   }

   @Test
   public void testVeryDeeplyNestedTreeSimplifedByFunction5() {
      String input = "(- 1 (* v2 (* (* v2 (* v2 (* v2 (* (* v2 (* v2 (* (* v2 (* (* 3 (* v2 (* v2 (* v2 (* (* v2 (* v2 (* v2 (* 3 v2)))) (* (* (* v2 (* v2 (* v2 (* v2 (* v2 (* v2 (* v2 (* (* 3 v2) (* 3 (* v2 v2)))))))))) (* 3 (* v2 v2))) (* v2 v2))))))) (* v2 (* v2 (* v2 (* v2 (* v2 (* v2 (* v2 (* (* v2 (* v2 (* v2 (* v2 (* v2 (* (* 3 v2) (* 3 (* v2 v2)))))))) (* 3 v2))))))))))) (* v2 (* v2 (* v2 (* v2 (* v2 (* (* v2 (* 3 (* v2 (* v2 (* (* v2 (* v2 (* v2 (* v2 (* v2 (* v2 (* v2 (* (* (* v2 (* v2 (* v2 (* (* 3 v2) (* 3 (- v2 v2)))))) (* v2 v2)) (* 3 v2))))))))) (* 3 (* v2 v2))))))) (* 3 v2)))))))))) (* (* v2 v2) (* v2 v2)))))) (* v2 v2))))";
      String expected = "1";
      when(input).expect(expected);
   }

   @Test
   public void testSanityCheckDoesFail() {
      // check that the "when/expect" pattern used in these tests does actually fail when the results are not as expected
      try {
         when("(+ v0 v1)").expect("(+ v1 v0)");
         fail();
      } catch (ComparisonFailure e) {
         assertEquals("9 vs. 9 expected:<(+ v[1 v0])> but was:<(+ v[0 v1])>", e.getMessage());
      }
   }

   private TestCase when(String input) {
      return new TestCase(input);
   }

   private static class TestCase {
      final String input;

      public TestCase(String input) {
         this.input = input;
      }

      void expect(String expectedOutput) {
         Node inputNode = readNode(input);
         Node simplifiedVersion = NodeSimplifier.simplify(inputNode);

         // test simplified version produces the same results as original (i.e. unsimplified) version
         Object[][] assignedValues = { { 0, 0, 0, 0, 0 }, { 1, 21, 8, -3, 3 }, { 2, 14, 4, 5, 6 }, { 3, -6, 2, 12, 4 }, { 7, 3, -1, 0, -6 },
               { -1, 9, 7, 4, 0 }, { -7, 0, -2, -3, 8 } };
         String simplifiedVersionString = writeNode(simplifiedVersion);
         for (Object[] assignedValue : assignedValues) {
            Assignments assignments = Assignments.createAssignments(assignedValue);
            assertEquals(simplifiedVersionString, evaluate(inputNode, assignments), evaluate(simplifiedVersion, assignments));
         }

         // test actual simplified version matches expected
         Node expectedNode = readNode(expectedOutput);
         String expectedVersionString = writeNode(expectedNode);
         assertEquals(expectedVersionString.length() + " vs. " + simplifiedVersionString.length(), expectedVersionString, simplifiedVersionString);
         assertEquals(simplifiedVersionString, expectedNode.toString(), simplifiedVersion.toString());
         assertEquals(expectedNode, simplifiedVersion);
      }

      Object evaluate(Node n, Assignments assignments) {
         return n.evaluate(assignments);
      }
   }
}
