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
package org.oakgp.primitive;

import org.oakgp.function.Function;
import org.oakgp.node.Node;
import org.oakgp.primitive.PrimitiveSet;
import org.oakgp.type.Types.Type;

public class DummyPrimitiveSet implements PrimitiveSet {
   @Override
   public boolean hasTerminals(Type type) {
      return true;
   }

   @Override
   public boolean hasFunctions(Type type) {
      return true;
   }

   @Override
   public Node nextTerminal(Type type) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Node nextAlternativeTerminal(Node current) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Function nextFunction(Type type) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Function nextAlternativeFunction(Function current) {
      throw new UnsupportedOperationException();
   }
}
