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
package org.oakgp.select;

import static org.oakgp.TestUtils.readNode;

import java.util.Arrays;

import org.oakgp.node.Node;

/**
 * An implementation of {@code NodeSelector} used by unit tests.
 * <p>
 * More convenient than mocking as it accepts {@code String} representations as an alternative to having to specify actual {@code Node} instances. e.g. Can do:
 *
 * <pre>
 * new DummyNodeSelector(&quot;(+ 9 5)&quot;)
 * </pre>
 *
 * instead of:
 *
 * <pre>
 * NodeSelector mockSelector = mock(NodeSelector.class);
 * given(mockSelector.next()).willReturn(new FunctionNode(new Add(), createArguments(new ConstantNode(9), new ConstantNode(5))))
 * </pre>
 */
public class DummyNodeSelector implements NodeSelector {
   private final Node[] nodes;
   private int ctr = 0;

   public static DummyNodeSelector repeat(int count, String... nodes) {
      return repeat(count, Arrays.stream(nodes).map(s -> readNode(s)).toArray(Node[]::new));
   }

   public static DummyNodeSelector repeat(int count, Node... nodes) {
      Node[] result = new Node[nodes.length * count];
      for (int i = 0; i < count; i++) {
         System.arraycopy(nodes, 0, result, i * nodes.length, nodes.length);
      }
      return new DummyNodeSelector(result);
   }

   public DummyNodeSelector(Node... nodes) {
      this.nodes = nodes;
   }

   public DummyNodeSelector(String... expressions) {
      this.nodes = new Node[expressions.length];
      for (int i = 0; i < expressions.length; i++) {
         nodes[i] = readNode(expressions[i]);
      }
   }

   @Override
   public Node next() {
      if (ctr == nodes.length) {
         throw new ArrayIndexOutOfBoundsException("Trying to access element: " + ctr + " of: " + Arrays.toString(nodes));
      }
      return nodes[ctr++];
   }

   public void assertEmpty() {
      if (ctr != nodes.length) {
         throw new IllegalStateException("Not all nodes have been selected, only read " + ctr + " of " + nodes.length);
      }
   }
}
