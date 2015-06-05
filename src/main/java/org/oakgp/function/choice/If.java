package org.oakgp.function.choice;

import static java.lang.Boolean.TRUE;
import static org.oakgp.Type.booleanType;
import static org.oakgp.node.NodeType.isConstant;

import java.util.function.Predicate;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.util.Utils;

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

      Predicate<Node> criteria = n -> n.equals(condition);
      Node simplifiedTrueBranch = trueBranch.replaceAll(criteria, n -> Utils.TRUE_NODE);
      Node simplifiedFalseBranch = falseBranch.replaceAll(criteria, n -> Utils.FALSE_NODE);
      if (trueBranch != simplifiedTrueBranch || falseBranch != simplifiedFalseBranch) {
         return new FunctionNode(this, condition, simplifiedTrueBranch, simplifiedFalseBranch);
      } else {
         return null;
      }
   }

   private int getOutcomeArgumentIndex(Arguments arguments, Assignments assignments) {
      return TRUE.equals(arguments.firstArg().evaluate(assignments)) ? TRUE_IDX : FALSE_IDX;
   }
}
