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

import static org.oakgp.examples.ant.AntMovement.isLeftAndRight;
import static org.oakgp.util.Void.VOID_CONSTANT;
import static org.oakgp.util.Void.VOID_TYPE;
import static org.oakgp.util.Void.isVoid;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.function.ImpureFunction;
import org.oakgp.function.Signature;
import org.oakgp.node.Node;
import org.oakgp.util.Void;

/** Executes two nodes in sequence. */
class BiSequence implements ImpureFunction {
   static final BiSequence BISEQUENCE = new BiSequence();

   private BiSequence() {
   }

   @Override
   public Signature getSignature() {
      return Signature.createSignature(VOID_TYPE, VOID_TYPE, VOID_TYPE);
   }

   @Override
   public Void evaluate(Arguments arguments, Assignments assignments) {
      arguments.firstArg().evaluate(assignments);
      arguments.secondArg().evaluate(assignments);
      return Void.VOID;
   }

   @Override
   public Node simplify(Arguments arguments) {
      Node firstArg = arguments.firstArg();
      Node secondArg = arguments.secondArg();
      if (isVoid(firstArg)) {
         return secondArg;
      } else if (isVoid(secondArg)) {
         return firstArg;
      } else if (isLeftAndRight(firstArg, secondArg)) {
         return VOID_CONSTANT;
      } else {
         return null;
      }
   }
}
