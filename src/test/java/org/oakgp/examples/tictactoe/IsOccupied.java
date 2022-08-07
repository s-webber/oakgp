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

import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.ChildNodes;

public class IsOccupied implements Function {
   private static final Signature SIGNATURE = createSignature(booleanType(), type("board"), type("possibleMove"), type("symbol"));

   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments) {
      Board board = arguments.first().evaluate(assignments);
      Move move = arguments.second().evaluate(assignments);
      Symbol symbol = arguments.third().evaluate(assignments);
      return board.isOccupied(move, symbol);
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }
}
