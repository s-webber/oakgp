package org.oakgp.examples.hanoi;

import static org.oakgp.Type.integerType;
import static org.oakgp.examples.hanoi.TowersOfHanoiExample.POLE_TYPE;
import static org.oakgp.examples.hanoi.TowersOfHanoiExample.STATE_TYPE;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.function.Function;

/** Returns the ID of the next disc that would be returned from a particular pole for a particular game state. */
class Next implements Function {
   private static final Signature SIGNATURE = Signature.createSignature(integerType(), STATE_TYPE, POLE_TYPE);

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }

   /**
    * @param arguments
    *           the first argument is a {@code TowersOfHanoi} representing a game state and the second argument is a {@code Pole}
    * @param assignments
    *           the values assigned to each of member of the variable set
    * @return the ID of the upper (i.e. top) disc of the specified pole, or {code 0} if there are no discs on the pole
    */
   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      TowersOfHanoi gameState = arguments.firstArg().evaluate(assignments);
      Pole pole = arguments.secondArg().evaluate(assignments);
      return gameState.upperDisc(pole);
   }
}
