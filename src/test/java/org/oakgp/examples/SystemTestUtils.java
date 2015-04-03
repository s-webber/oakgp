package org.oakgp.examples;

import static org.oakgp.TestUtils.createConstant;
import static org.oakgp.TestUtils.writeNode;

import java.util.Collection;
import java.util.Set;

import org.oakgp.FunctionSet;
import org.oakgp.NodeSimplifier;
import org.oakgp.RankedCandidate;
import org.oakgp.Signature;
import org.oakgp.TerminalSet;
import org.oakgp.Type;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
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
import org.oakgp.selector.NodeSelectorFactory;
import org.oakgp.selector.WeightedNodeSelectorFactory;
import org.oakgp.util.JavaUtilRandomAdapter;
import org.oakgp.util.NodeSet;
import org.oakgp.util.Random;

/** Utility classes for tests in sub-packages of {@code org.oakgp.examples}. */
public class SystemTestUtils {
   public static final Random RANDOM = new JavaUtilRandomAdapter();
   public static final NodeSelectorFactory SELECTOR_FACTORY = new WeightedNodeSelectorFactory(RANDOM);
   public static final FunctionSet ARITHMETIC_FUNCTION_SET = new FunctionSet(RANDOM, new Function[] { new Add(), new Subtract(), new Multiply() });
   public static final FunctionSet COMPARISON_FUNCTION_SET = new FunctionSet(RANDOM, new Function[] { new Add(), new Subtract(), new Multiply(),
         new LessThan(), new LessThanOrEqual(), new GreaterThan(), new GreaterThanOrEqual(), new Equal(), new NotEqual(), new If() });
   public static final int GENERATION_SIZE = 50;
   public static final int ELITISM_SIZE = 3;
   public static final double RATIO_VARIABLES = .6;

   public static Collection<Node> createInitialGeneration(FunctionSet functionSet, TerminalSet terminalSet, int size) {
      return createInitialGeneration(functionSet, terminalSet, size, Type.INTEGER);
   }

   public static Collection<Node> createInitialGeneration(FunctionSet functionSet, TerminalSet terminalSet, int size, Type type) {
      Set<Node> initialGeneration = new NodeSet();
      for (int i = 0; i < size; i++) {
         Node n = makeRandomTree(functionSet, terminalSet, 4, type);
         if (n.getType() != type) {
            throw new RuntimeException();
         }
         initialGeneration.add(n);
      }
      return initialGeneration;
   }

   public static Node makeRandomTree(FunctionSet functionSet, TerminalSet terminalSet, int depth) {
      return makeRandomTree(functionSet, terminalSet, depth, Type.INTEGER);
   }

   private static Node makeRandomTree(FunctionSet functionSet, TerminalSet terminalSet, int depth, Type type) {
      if (depth > 0 && RANDOM.nextDouble() < .5) {
         Function function = functionSet.next(type);
         Signature signature = function.getSignature();
         Node[] args = new Node[signature.getArgumentTypesLength()];
         for (int i = 0; i < args.length; i++) {
            Type argType = signature.getArgumentType(i);
            Node arg = makeRandomTree(functionSet, terminalSet, depth - 1, argType);
            args[i] = arg;
         }
         return new FunctionNode(function, args);
      } else {
         return terminalSet.next(type);
      }
   }

   public static ConstantNode[] createConstants(int numberOfConstants) {
      ConstantNode[] constants = new ConstantNode[numberOfConstants + 2];
      for (int i = 0; i < numberOfConstants; i++) {
         constants[i] = createConstant(i);
      }
      constants[numberOfConstants] = new ConstantNode(Boolean.TRUE, Type.BOOLEAN);
      constants[numberOfConstants + 1] = new ConstantNode(Boolean.FALSE, Type.BOOLEAN);
      return constants;
   }

   public static void printRankedCandidate(RankedCandidate candidate) {
      System.out.println("Best: " + candidate);
      System.out.println(writeNode(new NodeSimplifier().simplify(candidate.getNode())));
   }
}
