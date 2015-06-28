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
package org.oakgp.function.math;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.node.Node;

abstract class ArithmeticOperator implements Function {
   private final Signature signature;

   protected ArithmeticOperator(Type type) {
      signature = Signature.createSignature(type, type, type);
   }

   @Override
   public final Object evaluate(Arguments arguments, Assignments assignments) {
      return evaluate(arguments.firstArg(), arguments.secondArg(), assignments);
   }

   protected abstract Object evaluate(Node arg1, Node arg2, Assignments assignments);

   @Override
   public final Signature getSignature() {
      return signature;
   }
}
