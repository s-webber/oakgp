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
import static org.oakgp.type.CommonTypes.nullableType;
import static org.oakgp.type.Types.type;

import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.ChildNodes;

public class IfValidMove implements Function {
   private static final Signature SIGNATURE = null;

   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments) {
      Board board = arguments.first().evaluate(assignments);
      Move move = arguments.second().evaluate(assignments);
      if (board.isFree(move)) {
         return move;
      } else {
         return null;
      }
   }

   @Override
   public Signature getSignature() {
      return createSignature(nullableType(type("tictactoemove")), type("board"), type("possibleMove"));
   }
}
