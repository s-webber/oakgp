package org.oakgp.util;

import java.util.function.Function;
import java.util.function.Predicate;

import org.oakgp.Type;
import org.oakgp.node.Node;
import org.oakgp.node.NodeType;

public abstract class DummyNode implements Node {
   @Override
   public int getNodeCount() {
      throw new UnsupportedOperationException();
   }

   @Override
   public int getNodeCount(Predicate<Node> treeWalkerStrategy) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Node replaceAt(int index, Function<Node, Node> replacement) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Node replaceAt(int index, Function<Node, Node> replacement, Predicate<Node> treeWalkerStrategy) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Node replaceAll(Predicate<Node> criteria, java.util.function.Function<Node, Node> replacement) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Node getAt(int index) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Node getAt(int index, Predicate<Node> treeWalkerStrategy) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int getHeight() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Type getType() {
      throw new UnsupportedOperationException();
   }

   @Override
   public NodeType getNodeType() {
      throw new UnsupportedOperationException();
   }
}
