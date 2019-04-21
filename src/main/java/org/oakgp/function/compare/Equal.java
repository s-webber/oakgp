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

import static org.oakgp.util.NodeComparator.NODE_COMPARATOR;

import org.oakgp.Type;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

/**
 * Determines if two objects are equal.
 * <p>
 * <b>Note:</b> Equality is checked using {@code Comparable#compareTo(Object)} rather than {@code Object#equals(Object)}.
 */
public final class Equal extends ComparisonOperator {
   /** Constructs a function that compares two arguments of the specified type. */
   public Equal(Type type) {
      super(type, true);
   }

   @Override
   protected boolean evaluate(int diff) {
      return diff == 0;
   }

   @Override
   public Node simplify(ChildNodes children) {
      Node simplifiedVersion = super.simplify(children);
      if (simplifiedVersion == null && NODE_COMPARATOR.compare(children.first(), children.second()) > 0) {
         return new FunctionNode(this, children.second(), children.first());
      } else {
         return simplifiedVersion;
      }
   }

   @Override
   public String getDisplayName() {
      return "=";
   }
}
