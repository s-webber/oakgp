package org.oakgp.function.choice;

import static java.lang.Boolean.TRUE;
import static org.oakgp.Type.booleanType;
import static org.oakgp.node.NodeType.isConstant;
import static org.oakgp.node.NodeType.isFunction;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

/**
 * A selection operator.
 * <p>
 * Expects three arguments:
 * <ol>
 * <li>Conditional statement.</li>
 * <li>Value to evaluate to if the conditional statement is {@code true}.</li>
 * <li>Value to evaluate to if the conditional statement is {@code false}.</li>
 * </ol>
 */
public final class If implements Function {
   private static final int TRUE_IDX = 1;
   private static final int FALSE_IDX = 2;

   private final Signature signature;

   public If(Type type) {
      signature = Signature.createSignature(type, booleanType(), type, type);
   }

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      int index = getOutcomeArgumentIndex(arguments, assignments);
      return arguments.getArg(index).evaluate(assignments);
   }

   @Override
   public Signature getSignature() {
      return signature;
   }

   @Override
   public Node simplify(Arguments arguments) {
      Node trueBranch = arguments.secondArg();
      Node falseBranch = arguments.thirdArg();
      if (trueBranch.equals(falseBranch)) {
         return trueBranch;
      }

      Node condition = arguments.firstArg();
      if (isConstant(condition)) {
         int index = getOutcomeArgumentIndex(arguments, null);
         return index == TRUE_IDX ? trueBranch : falseBranch;
      }

      return removeRedundantIf(condition, arguments);
   }

   private Node removeRedundantIf(Node condition, Arguments arguments) {
      Node result = removeRedundantIf(condition, arguments, TRUE_IDX);
      if (result != null) {
         return result;
      } else {
         return removeRedundantIf(condition, arguments, FALSE_IDX);
      }
   }

   private Node removeRedundantIf(Node condition, Arguments arguments, int branchIdx) {
      Node branch = arguments.getArg(branchIdx);
      if (isFunction(branch)) {
         FunctionNode fn = (FunctionNode) branch;
         if (fn.getFunction() == this && fn.getArguments().firstArg().equals(arguments.firstArg())) {
            return new FunctionNode(this, arguments.replaceAt(branchIdx, fn.getArguments().secondArg()));
         }
      }
      return null;
   }

   private int getOutcomeArgumentIndex(Arguments arguments, Assignments assignments) {
      return TRUE.equals(arguments.firstArg().evaluate(assignments)) ? TRUE_IDX : FALSE_IDX;
   }
}
