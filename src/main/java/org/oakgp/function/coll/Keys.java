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

import static org.oakgp.type.CommonTypes.listType;
import static org.oakgp.type.CommonTypes.mapType;

import java.util.ArrayList;
import java.util.Map;

import org.oakgp.Arguments;
import org.oakgp.function.MapperFunction;
import org.oakgp.function.Signature;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;

public class Keys implements MapperFunction {
   private final Signature signature;

   public Keys() {
      Type value = Types.generic("Value");
      Type key = Types.generic("Key");
      signature = Signature.createSignature(listType(key), mapType(key, value));
   }

   @Override
   public Object evaluate(Arguments arguments) {
      Map<?, ?> a = arguments.first();
      return new ArrayList<>(a.keySet()); // TODO should we just return a.keySet() instead?
   }

   @Override
   public Signature getSignature() {
      return signature;
   }
}
