package org.oakgp.serialize;

import org.oakgp.Arguments;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.function.Operator;

/**
 * Creates {@code String} representations of {@code Node} instances.
 * <p>
 * e.g. The {@code Node}:
 *
 * <pre>
 * new FunctionNode(new Add(), createArguments(new ConstantNode(9), new ConstantNode(5))
 * </pre>
 *
 * will produce the {@code String}:
 *
 * <pre>
 * (+ 9 5)
 * </pre>
 */
public final class NodeWriter {
   private final SymbolMap symbolMap = new SymbolMap();

   public String writeNode(Node node) {
      StringBuilder sb = new StringBuilder();
      writeNode(node, sb);
      return sb.toString();
   }

   private void writeNode(Node node, StringBuilder sb) {
      if (node instanceof FunctionNode) {
         FunctionNode functionNode = (FunctionNode) node;
         Operator operator = functionNode.getOperator();
         Arguments arguments = functionNode.getArguments();
         sb.append('(').append(symbolMap.getDisplayName(operator));
         for (int i = 0; i < arguments.length(); i++) {
            sb.append(' ').append(writeNode(arguments.get(i)));
         }
         sb.append(')');
      } else {
         sb.append(node);
      }
   }
}
