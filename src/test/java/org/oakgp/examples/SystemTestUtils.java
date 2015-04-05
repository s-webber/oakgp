package org.oakgp.examples;

import static org.oakgp.NodeSimplifier.simplify;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.TestUtils.writeNode;
import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerType;

import java.util.Collection;
import java.util.Set;

import org.oakgp.ConstantSet;
import org.oakgp.FunctionSet;
import org.oakgp.PrimitiveSet;
import org.oakgp.RankedCandidate;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.function.choice.If;
import org.oakgp.function.compare.Equal;
import org.oakgp.function.compare.GreaterThan;
import org.oakgp.function.compare.GreaterThanOrEqual;
import org.oakgp.function.compare.LessThan;
import org.oakgp.function.compare.LessThanOrEqual;
import org.oakgp.function.compare.NotEqual;
import org.oakgp.function.math.Add;
import org.oakgp.function.math.Multiply;
import org.oakgp.function.math.Subtract;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.selector.NodeSelectorFactory;
import org.oakgp.selector.WeightedNodeSelectorFactory;
import org.oakgp.util.JavaUtilRandomAdapter;
import org.oakgp.util.NodeSet;
import org.oakgp.util.Random;

/** Utility classes for tests in sub-packages of {@code org.oakgp.examples}. */
public class SystemTestUtils {
   public static final Random RANDOM = new JavaUtilRandomAdapter();
   public static final NodeSelectorFactory SELECTOR_FACTORY = new WeightedNodeSelectorFactory(RANDOM);
   public static final FunctionSet ARITHMETIC_FUNCTION_SET = createArithmeticFunctionSet();
   public static final FunctionSet COMPARISON_FUNCTION_SET = createComparisonFunctionSet();
   public static final int GENERATION_SIZE = 50;
   public static final int ELITISM_SIZE = 3;
   public static final double RATIO_VARIABLES = .6;

   public static Collection<Node> createInitialGeneration(PrimitiveSet primitiveSet, int size) {
      return createInitialGeneration(primitiveSet, size, integerType());
   }

   public static Collection<Node> createInitialGeneration(PrimitiveSet primitiveSet, int size, Type type) {
      Set<Node> initialGeneration = new NodeSet();
      for (int i = 0; i < size; i++) {
         Node n = makeRandomTree(primitiveSet, 4, type);
         if (n.getType() != type) {
            throw new RuntimeException();
         }
         initialGeneration.add(n);
      }
      return initialGeneration;
   }

   public static Node makeRandomTree(PrimitiveSet primitiveSet, int depth) {
      return makeRandomTree(primitiveSet, depth, integerType());
   }

   private static Node makeRandomTree(PrimitiveSet primitiveSet, int depth, Type type) {
      if (depth > 0 && RANDOM.nextDouble() < .5) {
         Function function = primitiveSet.nextFunction(type);
         Signature signature = function.getSignature();
         Node[] args = new Node[signature.getArgumentTypesLength()];
         for (int i = 0; i < args.length; i++) {
            Type argType = signature.getArgumentType(i);
            Node arg = makeRandomTree(primitiveSet, depth - 1, argType);
            args[i] = arg;
         }
         return new FunctionNode(function, args);
      } else {
         return primitiveSet.nextTerminal(type);
      }
   }

   private static FunctionSet createArithmeticFunctionSet() {
      FunctionSet.Builder builder = new FunctionSet.Builder();

      builder.put("+", new Add());
      builder.put("-", new Subtract());
      builder.put("*", new Multiply());

      return builder.build();
   }

   private static FunctionSet createComparisonFunctionSet() {
      FunctionSet.Builder builder = new FunctionSet.Builder();

      builder.put("+", new Add());
      builder.put("-", new Subtract());
      builder.put("*", new Multiply());

      builder.put("<", new LessThan());
      builder.put("<=", new LessThanOrEqual());
      builder.put(">", new GreaterThan());
      builder.put(">=", new GreaterThanOrEqual());
      builder.put("=", new Equal());
      builder.put("!=", new NotEqual());

      builder.put("if", new If());

      return builder.build();
   }

   public static ConstantSet createConstants(int numberOfConstants) {
      ConstantNode[] constants = new ConstantNode[numberOfConstants + 2];
      for (int i = 0; i < numberOfConstants; i++) {
         constants[i] = integerConstant(i);
      }
      constants[numberOfConstants] = new ConstantNode(Boolean.TRUE, booleanType());
      constants[numberOfConstants + 1] = new ConstantNode(Boolean.FALSE, booleanType());
      return new ConstantSet(constants);
   }

   public static void printRankedCandidate(RankedCandidate candidate) {
      System.out.println("Best: " + candidate);
      System.out.println(writeNode(simplify(candidate.getNode())));
   }
}
