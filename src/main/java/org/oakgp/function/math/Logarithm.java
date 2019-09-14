/*
 * Copyright 2019 S. Webber
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

import static org.oakgp.type.CommonTypes.doubleType;

import org.oakgp.Arguments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;

public class Logarithm implements Function {
   private final Signature signature;

   public Logarithm() {
      // TODO make second argument a generic type that extends Number
      signature = Signature.createSignature(doubleType(), doubleType());
   }

   @Override
   public Double evaluate(Arguments arguments) {
      Number a = arguments.first();
      return Math.log(a.doubleValue());
   }

   @Override
   public Signature getSignature() {
      return signature;
   }

   @Override
   public String getDisplayName() {
      return "log";
   }
}
