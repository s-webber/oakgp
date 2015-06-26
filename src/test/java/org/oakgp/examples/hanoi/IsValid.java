package org.oakgp.examples.hanoi;

import static org.oakgp.Type.booleanType;
import static org.oakgp.examples.hanoi.TowersOfHanoiExample.MOVE_TYPE;
import static org.oakgp.examples.hanoi.TowersOfHanoiExample.STATE_TYPE;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.function.Function;

/** Determines if a move is a valid move for a particular game state. */
class IsValid implements Function {
   private final Signature signature;

   IsValid() {
      this.signature = Signature.createSignature(booleanType(), STATE_TYPE, MOVE_TYPE);
   }

   @Override
   public Signature getSignature() {
      return signature;
   }

   /**
    * Determines if a move is a valid move for a particular game state.
    *
    * @param arguments
    *           the first argument is a {@code TowersOfHanoi} representing a game state and the second argument is a {@code Move}
    * @return {@code true} if the specified move is a valid move for the specified game state, else false
    */
   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      TowersOfHanoi gameState = arguments.firstArg().evaluate(assignments);
      Move move = arguments.secondArg().evaluate(assignments);
      return gameState.move(move) != null;
   }
}
