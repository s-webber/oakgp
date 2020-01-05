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
package org.oakgp.function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.oakgp.type.CommonTypes.integerType;

import java.util.Map;

import org.junit.Test;
import org.oakgp.TestUtils;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.primitive.VariableSet;

public class RulesEngineUtilsTest {
   private final VariableSet variableSet = VariableSet.createVariableSet(integerType(), integerType(), integerType(), integerType());

   @Test
   public void testBuildEngine_twoNodes() {
      Node n1 = readNode("(!= v2 v3)");
      Node n2 = readNode("(!= v0 v1)");
      RulesEngine engine = RulesEngineUtils.buildEngine(n1, n2);

      assertFalse(engine.hasFact(readNode("(> v0 v1)")));
      engine.addFact(readNode("(!= v0 v1)"), true);
      engine.addFact(readNode("(>= v0 v1)"), true);
      assertTrue(engine.getFact(readNode("(> v0 v1)")));

      assertFalse(engine.hasFact(readNode("(> v2 v3)")));
      engine.addFact(readNode("(!= v2 v3)"), true);
      engine.addFact(readNode("(>= v2 v3)"), true);
      assertTrue(engine.getFact(readNode("(> v2 v3)")));
   }

   @Test
   public void testBuildEngine_constant() {
      RulesEngine engine = RulesEngineUtils.buildEngine(new ConstantNode(1, integerType()));
      assertTrue(engine.getFacts().isEmpty());
   }

   @Test
   public void testBuildEngine_true() {
      Node input = readNode("(= v0 v1)");
      RulesEngine engine = RulesEngineUtils.buildEngine(input, true);
      assertTrue(engine.getFact(input));
      assertFalse(engine.getFact(readNode("(!= v0 v1)")));
   }

   @Test
   public void testBuildEngine_false() {
      Node input = readNode("(= v0 v1)");
      RulesEngine engine = RulesEngineUtils.buildEngine(input, false);
      assertFalse(engine.getFact(input));
      assertTrue(engine.getFact(readNode("(!= v0 v1)")));
   }

   @Test
   public void testIsEqual_true() {
      Node n1 = readNode("(and (>= v1 v0) (>= v0 v1))");
      Node n2 = readNode("(= v0 v1)");
      assertTrue(RulesEngineUtils.isEqual(n1, n2));
      assertTrue(RulesEngineUtils.isEqual(n2, n1));
   }

   @Test
   public void testNotEqual_false() {
      Node n1 = readNode("(and (> v1 v0) (>= v0 v1))");
      Node n2 = readNode("(= v0 v1)");
      assertFalse(RulesEngineUtils.isEqual(n1, n2));
      assertFalse(RulesEngineUtils.isEqual(n2, n1));
   }

   @Test
   public void testIsNotEqual_true() {
      Node n1 = readNode("(= v0 v1)");
      Node n2 = readNode("(!= v0 v1)");
      assertTrue(RulesEngineUtils.isExactOpposites(n1, n2));
      assertTrue(RulesEngineUtils.isExactOpposites(n2, n1));
   }

   @Test
   public void testIsNotEqual_false() {
      Node n1 = readNode("(odd? v0)");
      Node n2 = readNode("(zero? v0)");
      assertFalse(RulesEngineUtils.isExactOpposites(n1, n2));
      assertFalse(RulesEngineUtils.isExactOpposites(n2, n1));
   }

   @Test
   public void testIsSubset() {
      Node n1 = TestUtils.readNode("(zero? v0)");
      Node n2 = TestUtils.readNode("(even? v0)");
      RulesEngine engine = RulesEngineUtils.buildEngine(n1, n2);
      assertTrue(RulesEngineUtils.isSubset(engine, n1, n2));
      assertFalse(RulesEngineUtils.isSubset(engine, n2, n1));
   }

   @Test
   public void testReplace() {
      Node input = readNode("(and (neg? v0) (and (even? v0) (odd? v1)))");
      RulesEngine engine = new RulesEngine();
      engine.addFact(readNode("(even? v0)"), true);
      engine.addFact(readNode("(odd? v1)"), false);

      Node actual = RulesEngineUtils.replace(engine, input);
      Node expected = readNode("(and (neg? v0) (and true false))");
      assertEquals(expected, actual);
   }

   @Test
   public void testIsTrue_true() {
      Node input = readNode("(or (even? v0) (odd? v0))");
      assertTrue(RulesEngineUtils.isTrue(input));
   }

   @Test
   public void testIsTrue_false() {
      Node input = readNode("(or (neg? v0) (pos? v0))");
      assertFalse(RulesEngineUtils.isTrue(input));
   }

   @Test
   public void testIsFalse_true() {
      Node input = readNode("(and (> v1 v0) (> v0 v1))");
      assertTrue(RulesEngineUtils.isFalse(input));
   }

   @Test
   public void testIsFalse_false() {
      Node input = readNode("(and (>= v1 v0) (>= v0 v1))");
      assertFalse(RulesEngineUtils.isFalse(input));
   }

   @Test
   public void testFindCommonTruths() {
      Node n1 = readNode("(> v1 v0)");
      Node n2 = readNode("(> v0 v1)");
      Map<Node, Boolean> result = RulesEngineUtils.findCommonTruths(n1, n2);
      assertEquals(2, result.size());
      assertTrue(result.get(readNode("(!= v0 v1)")));
      assertFalse(result.get(readNode("(= v0 v1)")));
   }

   @Test
   public void testFindCommonFalses() {
      Node n1 = readNode("(>= v1 v0)");
      Node n2 = readNode("(>= v0 v1)");
      Map<Node, Boolean> result = RulesEngineUtils.findCommonFalses(n1, n2);
      assertEquals(2, result.size());
      assertTrue(result.get(readNode("(!= v0 v1)")));
      assertFalse(result.get(readNode("(= v0 v1)")));
   }

   private Node readNode(String input) {
      return TestUtils.readNode(input, variableSet);
   }
}
