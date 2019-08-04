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
package org.oakgp.function.coll;

import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.listType;

import java.util.Collection;

import org.oakgp.Arguments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;

public class Sum implements Function {
   private final Signature signature = Signature.createSignature(integerType(), listType(integerType()));

   @Override
   public Object evaluate(Arguments arguments) {
      Collection<Integer> input = arguments.first();
      int result = 0;
      for (int i : input) {
         result += i;
      }
      return result;
   }

   @Override
   public Signature getSignature() {
      return signature;
   }
}
