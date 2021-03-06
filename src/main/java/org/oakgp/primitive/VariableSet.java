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
import org.oakgp.node.VariableNode;
import org.oakgp.util.Utils;

/** Represents the range of possible variables to use during a genetic programming run. */
public final class VariableSet {
   private final Map<Type, List<VariableNode>> variablesByType;
   private final VariableNode[] variables;

   /** Constructs a variable set containing variables of the specified types. */
   public static VariableSet createVariableSet(Type... variableTypes) {
      return new VariableSet(variableTypes);
   }

   private VariableSet(Type[] variableTypes) {
      this.variables = new VariableNode[variableTypes.length];
      for (int i = 0; i < variableTypes.length; i++) {
         this.variables[i] = new VariableNode(i, variableTypes[i]);
      }
      this.variablesByType = Utils.groupByType(this.variables);
   }

   /**
    * Returns a list of all variables in this set that are of the specified type.
    *
    * @param type
    *           the type to find matching variables of
    * @return a list of all variables in this set that are the specified type, or {@code null} if there are no variables of the required type in this set
    */
   public List<VariableNode> getByType(Type type) {
      // TODO should this return an empty list, rather than null, if no match found?
      return variablesByType.get(type);
   }

   /** Returns the {@code VariableNode} from this set that is associated with the specified ID. */
   public VariableNode getById(int id) {
      return variables[id];
   }
}
