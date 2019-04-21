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
package org.oakgp.function.compare;

import static org.oakgp.Type.booleanType;

import org.oakgp.Arguments;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.Node;
import org.oakgp.util.Utils;

abstract class ComparisonOperator implements Function {
   private final Signature signature;
   private final boolean equalsIsTrue;

   protected ComparisonOperator(Type type, boolean equalsIsTrue) {
      this.signature = Signature.createSignature(booleanType(), type, type);
      this.equalsIsTrue = equalsIsTrue;
   }

   @Override
   public final Object evaluate(Arguments arguments) {
      Comparable o1 = arguments.first();
      Comparable o2 = arguments.second();
      int diff = o1.compareTo(o2);
      if (evaluate(diff)) {
         return Boolean.TRUE;
      } else {
         return Boolean.FALSE;
      }
   }

   protected abstract boolean evaluate(int diff);

   @Override
   public final Signature getSignature() {
      return signature;
   }

   @Override
   public Node simplify(ChildNodes children) {
      if (children.first().equals(children.second())) {
         return equalsIsTrue ? Utils.TRUE_NODE : Utils.FALSE_NODE;
      } else {
         return null;
      }
   }
}
