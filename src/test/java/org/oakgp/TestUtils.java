package org.oakgp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerType;
import static org.oakgp.Type.stringType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.oakgp.function.Function;
import org.oakgp.function.choice.If;
import org.oakgp.function.choice.OrElse;
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
import org.oakgp.function.math.IntegerUtils;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;
import org.oakgp.serialize.NodeReader;
import org.oakgp.serialize.NodeWriter;

public class TestUtils {
   public static final VariableSet VARIABLE_SET = VariableSet.createVariableSet(createTypeArray(100));
   private static final FunctionSet FUNCTION_SET = createDefaultFunctionSet();

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

   public static FunctionNode readFunctionNode(String input) {
      return (FunctionNode) readNode(input);
   }

   public static Node readNode(String input) {
      return readNode(input, FUNCTION_SET, VARIABLE_SET);
   }

   public static Node readNode(String input, FunctionSet functionSet, VariableSet variableSet) {
      List<Node> outputs = readNodes(input, functionSet, variableSet);
      assertEquals(1, outputs.size());
      return outputs.get(0);
   }

   public static List<Node> readNodes(String input) {
      return readNodes(input, FUNCTION_SET, VARIABLE_SET);
   }

   public static List<Node> readNodes(String input, FunctionSet functionSet, VariableSet variableSet) {
      List<Node> outputs = new ArrayList<>();
      try (NodeReader nr = new NodeReader(input, functionSet, variableSet)) {
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

      functions.add(IntegerUtils.INTEGER_UTILS.getAdd());
      functions.add(IntegerUtils.INTEGER_UTILS.getSubtract());
      functions.add(IntegerUtils.INTEGER_UTILS.getMultiply());
      functions.add(IntegerUtils.INTEGER_UTILS.getDivide());

      functions.add(new LessThan(integerType()));
      functions.add(new LessThanOrEqual(integerType()));
      functions.add(new GreaterThan(integerType()));
      functions.add(new GreaterThanOrEqual(integerType()));
      functions.add(new Equal(integerType()));
      functions.add(new NotEqual(integerType()));

      functions.add(new If(integerType()));
      functions.add(new OrElse(stringType()));
      functions.add(new OrElse(integerType()));

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
      return VARIABLE_SET.getById(id);
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

   public static void assertNodeEquals(String expected, Node actual) {
      assertEquals(expected, writeNode(actual));
   }

   public static ConstantNode[] createEnumConstants(Class<? extends Enum<?>> e, Type t) {
      Enum<?>[] enumConstants = e.getEnumConstants();
      ConstantNode[] constants = new ConstantNode[enumConstants.length];
      for (int i = 0; i < enumConstants.length; i++) {
         constants[i] = new ConstantNode(enumConstants[i], t);
      }
      return constants;
   }
}
