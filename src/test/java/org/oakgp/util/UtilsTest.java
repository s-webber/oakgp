package org.oakgp.util;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.TestUtils.readNode;
import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerType;
import static org.oakgp.Type.stringType;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.oakgp.Type;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;

public class UtilsTest {
   @Test
   public void testGroupByType() {
      Node n1 = mockNode(integerType());
      Node n2 = mockNode(stringType());
      Node n3 = mockNode(integerType());
      Node n4 = mockNode(integerType());
      Node n5 = mockNode(booleanType());
      Node[] values = { n1, n2, n3, n4, n5 };
      Map<Type, List<Node>> groups = Utils.groupByType(values);
      assertEquals(3, groups.size());
      assertEquals(asList(n1, n3, n4), groups.get(integerType()));
      assertEquals(asList(n2), groups.get(stringType()));
      assertEquals(asList(n5), groups.get(booleanType()));
   }

   private Node mockNode(Type type) {
      Node mockNode = mock(Node.class);
      given(mockNode.getType()).willReturn(type);
      return mockNode;
   }

   @Test
   public void testGroupBy() {
      String[] values = { "aardvark", "apple", "bag", "cat", "cake", "caterpillar" };
      Map<Character, List<String>> groups = Utils.groupBy(values, (s) -> s.charAt(0));
      assertEquals(3, groups.size());
      assertEquals(asList("aardvark", "apple"), groups.get('a'));
      assertEquals(asList("bag"), groups.get('b'));
      assertEquals(asList("cat", "cake", "caterpillar"), groups.get('c'));
   }

   @Test
   public void testSelectSubNodeIndexFunctionNode() {
      assertSelectSubNodeIndex("(+ (+ 1 2) (+ 3 4))", 7, 3);
      assertSelectSubNodeIndex("(zero? 0)", 2, 1);
   }

   private void assertSelectSubNodeIndex(String input, int expectedNodeCount, int expectedIndex) {
      Node tree = readNode(input);
      assertEquals(expectedNodeCount, tree.getNodeCount());
      int actual = Utils.selectSubNodeIndex(tree, DummyRandom.getInt(expectedNodeCount - 1).returns(expectedIndex));
      assertEquals(expectedIndex, actual);
   }

   @Test
   public void testSelectSubNodeIndexTerminalNode() {
      ConstantNode terminal = integerConstant(1);
      assertEquals(0, Utils.selectSubNodeIndex(terminal, DummyRandom.EMPTY));
   }

}
