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
package org.oakgp.function.coll;

import static org.oakgp.Type.listType;
import static org.oakgp.Type.integerType;

import java.util.Collection;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.Node;

/** Determines the number of elements contained in a collection. */
public final class Count implements Function {
   private final Signature signature;

   /** Constructs a function to return the number of items in collections of the specified type. */
   public Count(Type t) {
      signature = Signature.createSignature(integerType(), listType(t));
   }

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      Collection<Node> a = arguments.firstArg().evaluate(assignments);
      return a.size();
   }

   @Override
   public Signature getSignature() {
      return signature;
   }
}
