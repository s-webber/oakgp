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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.oakgp.node.ConstantNode;
import org.oakgp.type.Types.Type;
import org.oakgp.util.TypeMap;

/** Represents the range of possible constants to use during a genetic programming run. */
public final class ConstantSet {
   private final List<ConstantNode> constants;
   private final TypeMap<ConstantNode> constantsByType;

   /** Constructs a constant set containing the specified constants. */
   public ConstantSet(ConstantNode... constants) { // TODO take list rather than varargs
      this.constants = Collections.unmodifiableList(Arrays.asList(constants));
      this.constantsByType = new TypeMap<>(this.constants, ConstantNode::getType);
   }

   /**
    * Returns a list of all constants in this set that are of the specified type.
    *
    * @param type
    *           the type to find matching constants of
    * @return a list of all constants in this set that are the specified type, or an empty list if there are no constants of the required type in this set
    */
   public List<ConstantNode> getByType(Type type) {
      return constantsByType.getByType(type);
   }

   public List<ConstantNode> getAll() {
      return constants;
   }
}
