package org.oakgp.node;

import java.util.function.Predicate;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Type;
import org.oakgp.function.Function;

/** Contains a function (operator) and the arguments (operands) to apply to it. */
public final class FunctionNode implements Node {
   private static final int PRIMES[] = { 2, 3, 5, 7, 11, 13, 19 };
   private final Function function;
   private final Arguments arguments;
   private final int nodeCount;
   private final int hashCode;

   /**
    * Constructs a new {@code FunctionNode} with the specified function function and arguments.
    *
    * @param function
    *           the function to associate with this {@code FunctionNode}
    * @param arguments
    *           the arguments (i.e. operands) to apply to {@code function} when evaluating this {@code FunctionNode}
    */
   public FunctionNode(Function function, Node... arguments) {
      this(function, Arguments.createArguments(arguments));
   }

   /**
    * Constructs a new {@code FunctionNode} with the specified function function and arguments.
    *
    * @param function
    *           the function to associate with this {@code FunctionNode}
    * @param arguments
    *           the arguments (i.e. operands) to apply to {@code function} when evaluating this {@code FunctionNode}
    */
   public FunctionNode(Function function, Arguments arguments) {
      this.function = function;
      this.arguments = arguments;
      this.nodeCount = calculateNodeCount(arguments);
      this.hashCode = (function.getClass().getName().hashCode() * 31) * createHashCode(arguments, nodeCount);
   }

   private static int calculateNodeCount(Arguments arguments) {
      int total = 1;
      for (int i = 0; i < arguments.getArgCount(); i++) {
         total += arguments.getArg(i).getNodeCount();
      }
      return total;
   }

   private static int createHashCode(Arguments arguments, int nodeCount) {
      int hashCode = 0;
      for (int i = 0; i < arguments.getArgCount(); i++) {
         hashCode += arguments.getArg(i).hashCode() * (PRIMES[i] + nodeCount);
      }
      return hashCode;
   }

   public Function getFunction() {
      return function;
   }

   public Arguments getArguments() {
      return arguments;
   }

   @Override
   public Object evaluate(Assignments assignments) {
      return function.evaluate(arguments, assignments);
   }

   @Override
   public Node replaceAt(int index, java.util.function.Function<Node, Node> replacement) {
      int total = 0;
      for (int i = 0; i < arguments.getArgCount(); i++) {
         Node node = arguments.getArg(i);
         int c = node.getNodeCount();
         if (total + c > index) {
            return new FunctionNode(function, arguments.replaceAt(i, node.replaceAt(index - total, replacement)));
         } else {
            total += c;
         }
      }
      return replacement.apply(this);
   }

   @Override
   public Node getAt(int index) {
      int total = 0;
      for (int i = 0; i < arguments.getArgCount(); i++) {
         Node node = arguments.getArg(i);
         int c = node.getNodeCount();
         if (total + c > index) {
            return arguments.getArg(i).getAt(index - total);
         } else {
            total += c;
         }
      }
      return this;
   }

   @Override
   public Node getAt(int index, Predicate<Node> treeWalkerStrategy) {
      int total = 0;
      for (int i = 0; i < arguments.getArgCount(); i++) {
         Node node = arguments.getArg(i);
         int c = node.getNodeCount(treeWalkerStrategy);
         if (total + c > index) {
            return arguments.getArg(i).getAt(index - total, treeWalkerStrategy);
         } else {
            total += c;
         }
      }
      if (!treeWalkerStrategy.test(this)) {
         throw new IllegalStateException();
      }
      return this;
   }

   @Override
   public int getNodeCount() {
      return nodeCount;
   }

   @Override
   public int getNodeCount(Predicate<Node> treeWalkerStrategy) {
      int total = treeWalkerStrategy.test(this) ? 1 : 0;
      for (int i = 0; i < arguments.getArgCount(); i++) {
         total += arguments.getArg(i).getNodeCount(treeWalkerStrategy);
      }
      return total;
   }

   @Override
   public int getDepth() {
      // TODO cache on first call
      int depth = 0;
      for (int i = 0; i < arguments.getArgCount(); i++) {
         depth = Math.max(depth, arguments.getArg(i).getDepth());
      }
      return depth + 1;
   }

   @Override
   public Type getType() {
      return function.getSignature().getReturnType();
   }

   @Override
   public int hashCode() {
      return hashCode;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (hashCode() != o.hashCode()) {
         return false;
      } else if (o instanceof FunctionNode) {
         FunctionNode fn = (FunctionNode) o;
         // TODO see how often we return false when we get here - as that indicates hashCode() could be improved
         return this.function.getClass().equals(fn.function.getClass()) && this.arguments.equals(fn.arguments);
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append('(').append(function.getClass().getName());
      for (int i = 0; i < arguments.getArgCount(); i++) {
         sb.append(' ').append(arguments.getArg(i));
      }
      return sb.append(')').toString();
   }
}
