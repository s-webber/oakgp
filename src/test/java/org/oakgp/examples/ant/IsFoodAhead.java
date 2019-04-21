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
package org.oakgp.examples.ant;

import org.oakgp.Arguments;
import org.oakgp.Type;
import org.oakgp.function.ImpureFunction;
import org.oakgp.function.Signature;

/** Returns {@code true} if the square the ant is facing contains food, else {@code false}. */
class IsFoodAhead implements ImpureFunction {
   @Override
   public Signature getSignature() {
      return Signature.createSignature(Type.booleanType(), MutableState.STATE_TYPE);
   }

   @Override
   public Boolean evaluate(Arguments arguments) {
      MutableState state = arguments.first();
      return state.isFoodAhead();
   }
}
