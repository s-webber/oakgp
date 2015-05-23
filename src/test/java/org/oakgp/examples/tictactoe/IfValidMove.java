package org.oakgp.examples.tictactoe;

import static org.oakgp.Signature.createSignature;
import static org.oakgp.Type.nullableType;
import static org.oakgp.Type.type;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.function.Function;

public class IfValidMove implements Function {
   private static final Signature SIGNATURE = createSignature(nullableType(type("move")), type("board"), type("possibleMove"));

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      Board board = arguments.firstArg().evaluate(assignments);
      Move move = arguments.secondArg().evaluate(assignments);
      if (board.isFree(move)) {
         return move;
      } else {
         return null;
      }
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }
}
