package org.oakgp.examples.tictactoe;

import static org.oakgp.Signature.createSignature;
import static org.oakgp.Type.optionalType;
import static org.oakgp.Type.type;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.function.Function;

final class GetWinningMove implements Function {
   private static final Signature SIGNATURE = createSignature(optionalType(type("move")), type("board"), type("symbol"));

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      Board board = arguments.firstArg().evaluate(assignments);
      Symbol symbol = arguments.secondArg().evaluate(assignments);
      return board.getWinningMove(symbol);
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }

   @Override
   public String getDisplayName() {
      return "winner";
   }
}
