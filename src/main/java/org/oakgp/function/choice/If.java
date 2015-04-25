package org.oakgp.function.choice;

import static java.lang.Boolean.TRUE;
import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerType;
import static org.oakgp.util.Utils.isConstant;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.function.Function;
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
   private static final Signature SIGNATURE = Signature.createSignature(integerType(), booleanType(), integerType(), integerType());

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      int index = getOutcomeArgumentIndex(arguments, assignments);
      return arguments.getArg(index).evaluate(assignments);
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }

   @Override
   public Node simplify(Arguments arguments) {
      if (arguments.secondArg().equals(arguments.thirdArg())) {
         return arguments.secondArg();
      } else if (isConstant(arguments.firstArg())) {
         int index = getOutcomeArgumentIndex(arguments, null);
         return arguments.getArg(index);
      } else {
         return null;
      }
   }

   private int getOutcomeArgumentIndex(Arguments arguments, Assignments assignments) {
      return TRUE.equals(arguments.firstArg().evaluate(assignments)) ? 1 : 2;
   }
}
