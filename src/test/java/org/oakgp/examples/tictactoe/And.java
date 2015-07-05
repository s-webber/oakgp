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

import static java.lang.Boolean.TRUE;
import static org.oakgp.Type.booleanType;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.Node;

class And implements Function {
   private static final Signature SIGNATURE = Signature.createSignature(booleanType(), booleanType(), booleanType());

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      return isTrue(arguments.firstArg(), assignments) && isTrue(arguments.secondArg(), assignments);
   }

   private boolean isTrue(Node node, Assignments assignments) {
      return TRUE.equals(node.evaluate(assignments));
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }
}
