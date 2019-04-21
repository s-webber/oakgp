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

import java.util.function.Consumer;

import org.oakgp.Arguments;
import org.oakgp.function.Function;
import org.oakgp.function.ImpureFunction;
import org.oakgp.function.Signature;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.NodeType;
import org.oakgp.util.Void;

/** Mutates the state of a {@code MutableState} object. */
final class AntMovement implements ImpureFunction {
   /** Move the ant forward one square. */
   static final AntMovement FORWARD = new AntMovement("forward", MutableState::forward);
   /** Turns the ant to the left. */
   static final AntMovement LEFT = new AntMovement("left", MutableState::left);
   /** Turns the ant to the right. */
   static final AntMovement RIGHT = new AntMovement("right", MutableState::right);

   private final String displayName;
   private final Consumer<MutableState> movement;

   private AntMovement(String displayName, Consumer<MutableState> movement) {
      this.displayName = displayName;
      this.movement = movement;
   }

   @Override
   public Signature getSignature() {
      return Signature.createSignature(Void.VOID_TYPE, MutableState.STATE_TYPE);
   }

   @Override
   public Void evaluate(Arguments arguments) {
      MutableState state = arguments.first();
      movement.accept(state);
      return Void.VOID;
   }

   @Override
   public String getDisplayName() {
      return displayName;
   }

   static boolean isLeftAndRight(Node firstArg, Node secondArg) {
      Function f1 = getFunction(firstArg);
      Function f2 = getFunction(secondArg);
      if (f1 == LEFT && f2 == RIGHT) {
         return true;
      } else if (f1 == RIGHT && f2 == LEFT) {
         return true;
      } else {
         return false;
      }
   }

   static boolean areAllSame(AntMovement function, Node firstArg, Node secondArg, Node thirdArg) {
      Function f1 = getFunction(firstArg);
      Function f2 = getFunction(secondArg);
      Function f3 = getFunction(thirdArg);
      return f1 == function && f2 == function && f3 == function;
   }

   private static Function getFunction(Node n) {
      if (NodeType.isFunction(n)) {
         return ((FunctionNode) n).getFunction();
      } else {
         throw new IllegalStateException(n.toString());
      }
   }
}
