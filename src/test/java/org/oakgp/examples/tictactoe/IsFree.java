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
package org.oakgp.examples.tictactoe;

import static org.oakgp.function.Signature.createSignature;
import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.Types.type;

import org.oakgp.Arguments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;

public class IsFree implements Function {
   private static final Signature SIGNATURE = createSignature(booleanType(), type("board"), type("possibleMove"));

   @Override
   public Object evaluate(Arguments arguments) {
      Board board = arguments.first();
      Move move = arguments.second();
      return board.isFree(move);
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }
}
