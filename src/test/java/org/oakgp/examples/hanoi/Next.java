package org.oakgp.examples.hanoi;

import static org.oakgp.Type.integerType;
import static org.oakgp.examples.hanoi.TowersOfHanoiExample.POLE_TYPE;
import static org.oakgp.examples.hanoi.TowersOfHanoiExample.STATE_TYPE;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.function.Function;

class Next implements Function {
   private static final Signature SIGNATURE = Signature.createSignature(integerType(), STATE_TYPE, POLE_TYPE);

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      TowersOfHanoi gameState = arguments.firstArg().evaluate(assignments);
      Pole pole = arguments.secondArg().evaluate(assignments);
      return gameState.upperDisc(pole);
   }
}
