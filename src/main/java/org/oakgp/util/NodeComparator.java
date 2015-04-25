package org.oakgp.util;

import java.util.Comparator;

import org.oakgp.node.Node;
import org.oakgp.node.NodeType;

/** An implementation of {@code Comparator} for comparing instances of {@link Node}. */
public final class NodeComparator implements Comparator<Node> {
   /** Singleton instance. */
   public static final NodeComparator NODE_COMPARATOR = new NodeComparator();

   /** Private constructor to force use of {@link #notify()}. */
   private NodeComparator() {
      // do nothing
   }

   @Override
   public int compare(Node o1, Node o2) {
      NodeType t1 = o1.getNodeType();
      NodeType t2 = o2.getNodeType();

      if (t1 == t2) {
         int i = o1.getType().compareTo(o2.getType());
         if (i == 0) {
            int i1 = o1.hashCode();
            int i2 = o2.hashCode();
            if (i1 == i2) {
               return 0;
            } else if (i1 < i2) {
               return -1;
            } else {
               return 1;
            }
         } else {
            return i;
         }
      } else if (t1 == NodeType.CONSTANT) {
         return -1;
      } else if (t2 == NodeType.CONSTANT) {
         return 1;
      } else if (t1 == NodeType.FUNCTION) {
         return 1;
      } else if (t2 == NodeType.FUNCTION) {
         return -1;
      } else {
         throw new IllegalStateException();
      }
   }
}
