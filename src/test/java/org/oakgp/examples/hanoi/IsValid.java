package org.oakgp.examples.hanoi;

import static org.oakgp.Type.booleanType;
import static org.oakgp.examples.hanoi.TowersOfHanoiExample.MOVE_TYPE;
import static org.oakgp.examples.hanoi.TowersOfHanoiExample.STATE_TYPE;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.function.Function;

class IsValid implements Function {
   private final Signature signature;

   IsValid() {
      this.signature = Signature.createSignature(booleanType(), STATE_TYPE, MOVE_TYPE);
   }

   @Override
   public Signature getSignature() {
      return signature;
   }

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      TowersOfHanoi gameState = arguments.firstArg().evaluate(assignments);
      Move move = arguments.secondArg().evaluate(assignments);
      return gameState.move(move) != null;
   }
}
