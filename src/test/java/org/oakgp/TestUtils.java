package org.oakgp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.oakgp.function.Function;
import org.oakgp.function.choice.If;
import org.oakgp.function.classify.IsNegative;
import org.oakgp.function.classify.IsPositive;
import org.oakgp.function.classify.IsZero;
import org.oakgp.function.coll.Count;
import org.oakgp.function.compare.Equal;
import org.oakgp.function.compare.GreaterThan;
import org.oakgp.function.compare.GreaterThanOrEqual;
import org.oakgp.function.compare.LessThan;
import org.oakgp.function.compare.LessThanOrEqual;
import org.oakgp.function.compare.NotEqual;
import org.oakgp.function.hof.Filter;
import org.oakgp.function.hof.Reduce;
import org.oakgp.function.math.Add;
import org.oakgp.function.math.Multiply;
import org.oakgp.function.math.Subtract;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;
import org.oakgp.serialize.NodeReader;
import org.oakgp.serialize.NodeWriter;

public class TestUtils {
   public static void assertVariable(int expectedId, Node node) {
      assertTrue(node instanceof VariableNode);
      assertEquals(expectedId, ((VariableNode) node).getId());
   }

   public static void assertConstant(Object expectedValue, Node node) {
      assertTrue(node instanceof ConstantNode);
      assertEquals(expectedValue, ((ConstantNode) node).evaluate(null));
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   public static void assertUnmodifiable(List list) {
      assertEquals("java.util.Collections$UnmodifiableRandomAccessList", list.getClass().getName());
      try {
         list.add(new Object());
         fail();
      } catch (UnsupportedOperationException e) {
         // expected
      }
   }

   public static String writeNode(Node input) {
      return new NodeWriter().writeNode(input);
   }

   public static Node readNode(String input) {
      List<Node> outputs = readNodes(input);
      assertEquals(1, outputs.size());
      return outputs.get(0);
   }

   public static List<Node> readNodes(String input) {
      List<Node> outputs = new ArrayList<>();
      try (NodeReader nr = new NodeReader(input, createDefaultFunctionSet(), createVariableNodes(100))) {
         while (!nr.isEndOfStream()) {
            outputs.add(nr.readNode());
         }
      } catch (IOException e) {
         throw new RuntimeException("IOException caught reading: " + input, e);
      }
      return outputs;
   }

   private static FunctionSet createDefaultFunctionSet() {
      List<Function> functions = new ArrayList<>();

      functions.add(new Add());
      functions.add(new Subtract());
      functions.add(new Multiply());

      functions.add(new LessThan());
      functions.add(new LessThanOrEqual());
      functions.add(new GreaterThan());
      functions.add(new GreaterThanOrEqual());
      functions.add(new Equal());
      functions.add(new NotEqual());

      functions.add(new If());

      functions.add(new Reduce(integerType()));
      functions.add(new Filter(integerType()));
      functions.add(new org.oakgp.function.hof.Map(integerType(), booleanType()));

      functions.add(new IsPositive());
      functions.add(new IsNegative());
      functions.add(new IsZero());

      functions.add(new Count(integerType()));
      functions.add(new Count(booleanType()));

      return new FunctionSet(functions.toArray(new Function[functions.size()]));
   }

   private static VariableNode[] createVariableNodes(int size) {
      VariableNode[] v = new VariableNode[size];
      for (int i = 0; i < size; i++) {
         v[i] = new VariableNode(i, Type.integerType());
      }
      return v;
   }

   public static Arguments createArguments(String... expressions) {
      Node[] args = new Node[expressions.length];
      for (int i = 0; i < expressions.length; i++) {
         args[i] = readNode(expressions[i]);
      }
      return Arguments.createArguments(args);
   }

   public static ConstantNode integerConstant(int value) {
      return new ConstantNode(value, Type.integerType());
   }

   public static ConstantNode booleanConstant(Boolean value) {
      return new ConstantNode(value, Type.booleanType());
   }

   public static ConstantNode stringConstant(String value) {
      return new ConstantNode(value, Type.stringType());
   }

   public static VariableNode createVariable(int id) {
      return new VariableNode(id, Type.integerType());
   }

   public static Type[] createTypeArray(int size) {
      Type[] a = new Type[size];
      for (int i = 0; i < size; i++) {
         a[i] = Type.integerType();
      }
      return a;
   }

   public static void assertRankedCandidate(RankedCandidate actual, Node expectedNode, double expectedFitness) {
      assertSame(expectedNode, actual.getNode());
      assertEquals(expectedFitness, actual.getFitness(), 0);
   }
}
