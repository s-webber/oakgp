package org.oakgp.examples.tictactoe;

import static org.oakgp.Signature.createSignature;
import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.type;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.function.Function;

public class IsFree implements Function {
   private static final Signature SIGNATURE = createSignature(booleanType(), type("board"), type("possibleMove"));

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      Board board = arguments.firstArg().evaluate(assignments);
      Move move = arguments.secondArg().evaluate(assignments);
      return board.isFree(move);
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }
}
