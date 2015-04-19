package org.oakgp.selector;

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
