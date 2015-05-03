package org.oakgp.examples.tictactoe;

import static org.oakgp.Signature.createSignature;
import static org.oakgp.Type.type;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.function.Function;

public class GetAnyMove implements Function {
   private static final Signature SIGNATURE = createSignature(type("move"), type("board"));

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      Board board = arguments.firstArg().evaluate(assignments);
      return board.getFreeMove();
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }

   @Override
   public String getDisplayName() {
      return "any";
   }
}
