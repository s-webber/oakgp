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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.oakgp.Arguments;
import org.oakgp.function.Function;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

/**
 * Creates {@code String} representations of {@code Node} instances.
 * <p>
 * e.g. Calling {@link #writeNode(Node)} with the {@code Node}:
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
   private final Map<Class<?>, java.util.function.Function<Object, String>> writers = new HashMap<>();

   /** Creates a {@code NodeWriter} for the purpose of generating textual representations of {@code Node} instances. */
   public NodeWriter() {
      writers.put(Long.class, o -> o + "L");
      writers.put(BigInteger.class, o -> o + "I");
      writers.put(BigDecimal.class, o -> o + "D");
      writers.put(Function.class, o -> ((Function) o).getDisplayName());
      writers.put(Arguments.class, o -> writeArguments((Arguments) o));
   }

   /** Returns a {@code String} representation of the specified {@code Node}. */
   public String writeNode(Node node) {
      switch (node.getNodeType()) {
         case FUNCTION:
            return writeFunctionNode(node);
         case VARIABLE:
            return writeVariableNode(node);
         case CONSTANT:
            return writeConstantNode(node);
         default:
            throw new IllegalArgumentException("Unknown NodeType: " + node.getType());
      }
   }

   private String writeFunctionNode(Node node) {
      FunctionNode functionNode = (FunctionNode) node;
      Function function = functionNode.getFunction();
      Arguments arguments = functionNode.getArguments();
      StringBuilder sb = new StringBuilder();
      sb.append('(').append(function.getDisplayName());
      for (int i = 0; i < arguments.getArgCount(); i++) {
         sb.append(' ').append(writeNode(arguments.getArg(i)));
      }
      return sb.append(')').toString();
   }

   private String writeVariableNode(Node node) {
      return node.toString();
   }

   private String writeConstantNode(Node node) {
      Object value = node.evaluate(null);
      return writers.entrySet().stream().filter(e -> e.getKey().isInstance(value)).map(Map.Entry::getValue).findFirst().orElse(String::valueOf).apply(value);
   }

   private String writeArguments(Arguments args) {
      StringBuilder sb = new StringBuilder();
      sb.append('[');
      for (int i = 0; i < args.getArgCount(); i++) {
         if (i != 0) {
            sb.append(' ');
         }
         sb.append(writeNode(args.getArg(i)));
      }
      return sb.append(']').toString();
   }
}
