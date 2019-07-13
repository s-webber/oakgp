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
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.type.Types.Type;

abstract class ArithmeticOperator<T extends Comparable<T>> implements Function {
   private final Signature signature;

   protected ArithmeticOperator(Type type) {
      signature = Signature.createSignature(type, type, type);
   }

   @Override
   public final T evaluate(Arguments arguments) {
      return evaluate(arguments.first(), arguments.second());
   }

   protected abstract T evaluate(T arg1, T arg2);

   @Override
   public final Signature getSignature() {
      return signature;
   }
}
