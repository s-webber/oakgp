package org.oakgp.node;

import java.util.function.Function;
import java.util.function.Predicate;

abstract class TerminalNode implements Node {
   @Override
   public final int getDepth() {
      return 1;
   }

   @Override
   public final int getNodeCount() {
      return 1;
   }

   @Override
   public final int getNodeCount(Predicate<Node> treeWalkerStrategy) {
      return treeWalkerStrategy.test(this) ? 1 : 0;
   }

   @Override
   public final Node replaceAt(int index, Function<Node, Node> replacement) {
      return replacement.apply(this);
   }

   @Override
   public final Node replaceAt(int index, Function<Node, Node> replacement, Predicate<Node> treeWalkerStrategy) {
      // TODO review what correct behaviour should be
      return replaceAt(index, replacement);
   }

   @Override
   public final Node getAt(int index) {
      return this;
   }

   @Override
   public final Node getAt(int index, Predicate<Node> treeWalkerStrategy) {
      // TODO review what correct behaviour should be
      return getAt(index);
   }
}
