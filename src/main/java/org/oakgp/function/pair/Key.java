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
package org.oakgp.function.pair;

import static org.oakgp.type.CommonTypes.entryType;

import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.AbstractDefinedFunctions;
import org.oakgp.node.ChildNodes;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;

public final class Key implements Function {
   private final Signature signature;

   public Key() { // TODO make private and add singleton method
      Type key = Types.generic("KeyType");
      Type value = Types.generic("ValueType");
      signature = Signature.createSignature(key, entryType(key, value));
   }

   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments, AbstractDefinedFunctions adfs) {
      Pair p = arguments.first().evaluate(assignments, adfs);
      return p.first();
   }

   @Override
   public Signature getSignature() {
      return signature;
   }
}
