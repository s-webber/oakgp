package org.oakgp.examples.tictactoe;

import static org.oakgp.Signature.createSignature;
import static org.oakgp.Type.optionalType;
import static org.oakgp.Type.type;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.function.Function;

final class GetPossibleMove implements Function {
   private static final Signature SIGNATURE = createSignature(optionalType(type("move")), type("board"));

   private final String displayName;
   private final java.util.function.Function<Board, Move> f;

   public GetPossibleMove(String displayName, java.util.function.Function<Board, Move> f) {
      this.displayName = displayName;
      this.f = f;
   }

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      Board board = arguments.firstArg().evaluate(assignments);
      return f.apply(board);
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }

   @Override
   public String getDisplayName() {
      return displayName;
   }
}
