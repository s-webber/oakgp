/*
 * Copyright 2015 S. Webber
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
package org.oakgp.serialize;

import static org.oakgp.Type.bigDecimalType;
import static org.oakgp.Type.bigIntegerType;
import static org.oakgp.Type.longType;

import java.util.HashMap;
import java.util.Map;

import org.oakgp.Arguments;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

/**
 * Creates {@code String} representations of {@code Node} instances.
 * <p>
 * e.g. The {@code Node}:
 *
 * <pre>
 * new FunctionNode(new Add(), new ConstantNode(9), new ConstantNode(5))
 * </pre>
 *
 * will produce the {@code String}:
 *
 * <pre>
 * (+ 9 5)
 * </pre>
 */
public final class NodeWriter {
   private static final Map<Type, String> SUFFIXES = new HashMap<>();
   static {
      SUFFIXES.put(longType(), "L");
      SUFFIXES.put(bigIntegerType(), "I");
      SUFFIXES.put(bigDecimalType(), "D");
   }

   /** Returns a {@code String} representation of the specified node. */
   public String writeNode(Node node) {
      StringBuilder sb = new StringBuilder();
      writeNode(node, sb);
      return sb.toString();
   }

   private void writeNode(Node node, StringBuilder sb) {
      switch (node.getNodeType()) {
         case FUNCTION:
            writeFunctionNode(node, sb);
            break;
         case VARIABLE:
            writeVariableNode(node, sb);
            break;
         case CONSTANT:
            writeConstantNode(node, sb);
            break;
         default:
            throw new IllegalArgumentException("Unknown NodeType: " + node.getType());
      }
   }

   private void writeFunctionNode(Node node, StringBuilder sb) {
      FunctionNode functionNode = (FunctionNode) node;
      Function function = functionNode.getFunction();
      Arguments arguments = functionNode.getArguments();
      sb.append('(').append(function.getDisplayName());
      for (int i = 0; i < arguments.getArgCount(); i++) {
         sb.append(' ').append(writeNode(arguments.getArg(i)));
      }
      sb.append(')');
   }

   private void writeVariableNode(Node node, StringBuilder sb) {
      sb.append(node.toString());
   }

   private void writeConstantNode(Node node, StringBuilder sb) {
      sb.append(node.toString());
      String suffix = SUFFIXES.get(node.getType());
      if (suffix != null) {
         sb.append(suffix);
      }
   }
}
