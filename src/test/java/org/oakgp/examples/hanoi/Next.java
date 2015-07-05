/*
 * Copyright 2015 S. Webber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oakgp.examples.hanoi;

import static org.oakgp.Type.integerType;
import static org.oakgp.examples.hanoi.TowersOfHanoiExample.POLE_TYPE;
import static org.oakgp.examples.hanoi.TowersOfHanoiExample.STATE_TYPE;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;

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
