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

import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.listType;

import java.util.Collection;

import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.ChildNodes;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;

/**
 * Determines if a value is contained in a collection.
 * <p>
 * Returns true if the value specified by the first argument is contained in the collection specified by the second
 * argument.
 */
public final class Contains implements Function {
   private final Signature signature;

   public Contains() {
      Type type = Types.generic("ElementType");
      signature = Signature.createSignature(booleanType(), listType(type), type);
   }

   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments) {
      Collection<?> collection = arguments.first().evaluate(assignments);
      return collection.contains(arguments.second().evaluate(assignments));
   }

   @Override
   public Signature getSignature() {
      return signature;
   }
}
