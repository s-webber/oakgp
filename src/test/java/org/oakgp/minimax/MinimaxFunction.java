/*
 * Copyright 2022 S. Webber
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
package org.oakgp.minimax;

import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.AutomaticallyDefinedFunctions;
import org.oakgp.node.ChildNodes;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;

public class MinimaxFunction implements Function { // TODO move from test to main
   private static final Type MINIMAX_NODE_TYPE = Types.declareType("minimaxGameState");
   private static final Signature SIGNATURE = Signature.createSignature(MINIMAX_NODE_TYPE, MINIMAX_NODE_TYPE);

   private final int depth;

   public MinimaxFunction(int depth) {
      this.depth = depth;
   }

   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments, AutomaticallyDefinedFunctions adfs) {
      return MinimaxSearch.minimax((MinimaxGameState) assignments.get(0), arguments.first(), depth);
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }
}
