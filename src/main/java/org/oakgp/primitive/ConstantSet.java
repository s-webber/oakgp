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

import java.util.List;
import java.util.Map;

import org.oakgp.Type;
import org.oakgp.node.ConstantNode;
import org.oakgp.util.Utils;

/** Represents the range of possible constants to use during a genetic programming run. */
public final class ConstantSet {
   private final Map<Type, List<ConstantNode>> constantsByType;

   /** Constructs a constant set containing the specified constants. */
   public ConstantSet(ConstantNode... constants) {
      constantsByType = Utils.groupByType(constants);
   }

   /**
    * Returns a list of all constants in this set that are of the specified type.
    *
    * @param type
    *           the type to find matching constants of
    * @return a list of all constants in this set that are the specified type, or {@code null} if there are no constants of the required type in this set
    */
   public List<ConstantNode> getByType(Type type) {
      // TODO should this return an empty list, rather than null, if no match found?
      return constantsByType.get(type);
   }
}
