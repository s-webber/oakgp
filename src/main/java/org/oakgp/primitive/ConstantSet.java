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

   public ConstantSet(ConstantNode... constants) {
      constantsByType = Utils.groupByType(constants);
   }

   public List<ConstantNode> getByType(Type type) {
      return constantsByType.get(type);
   }
}
