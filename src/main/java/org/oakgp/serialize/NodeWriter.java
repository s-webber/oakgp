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

import static org.oakgp.node.NodeType.isFunction;

import org.oakgp.Arguments;
import org.oakgp.function.Function;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

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
   public String writeNode(Node node) {
      StringBuilder sb = new StringBuilder();
      writeNode(node, sb);
      return sb.toString();
   }

   private void writeNode(Node node, StringBuilder sb) {
      if (isFunction(node)) {
         FunctionNode functionNode = (FunctionNode) node;
         Function function = functionNode.getFunction();
         Arguments arguments = functionNode.getArguments();
         sb.append('(').append(function.getDisplayName());
         for (int i = 0; i < arguments.getArgCount(); i++) {
            sb.append(' ').append(writeNode(arguments.getArg(i)));
         }
         sb.append(')');
      } else {
         sb.append(node);
      }
   }
}
