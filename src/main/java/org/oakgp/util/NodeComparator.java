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
package org.oakgp.util;

import java.io.Serializable;
import java.util.Comparator;

import org.oakgp.node.Node;
import org.oakgp.node.NodeType;

/** An implementation of {@code Comparator} for comparing instances of {@link Node}. */
public final class NodeComparator implements Comparator<Node>, Serializable {
   private static final long serialVersionUID = 1L;

   /** Singleton instance. */
   public static final NodeComparator NODE_COMPARATOR = new NodeComparator();

   /** Private constructor to force use of {@link #NODE_COMPARATOR}. */
   private NodeComparator() {
      // do nothing
   }

   // TODO review this implementation
   @Override
   public int compare(Node o1, Node o2) {
      NodeType t1 = o1.getNodeType();
      NodeType t2 = o2.getNodeType();

      if (t1 == t2) {
         int i = o1.getType().getName().compareTo(o2.getType().getName());
         if (i == 0) {
            return Integer.compare(o1.hashCode(), o2.hashCode());
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
