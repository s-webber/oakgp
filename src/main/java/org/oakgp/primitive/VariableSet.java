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
import java.util.List;

import org.oakgp.function.pair.Pair;
import org.oakgp.node.VariableNode;
import org.oakgp.type.Types.Type;
import org.oakgp.util.TypeMap;

/** Represents the range of possible variables to use during a genetic programming run. */
public final class VariableSet {
   private final TypeMap<VariableNode> variablesByType;
   private final VariableNode[] variables;

   public static VariableSet createVariableSet(VariableNode[] variables) {
      return new VariableSet(variables);
   }

   /** Constructs a variable set containing variables of the specified types. */
   public static VariableSet createVariableSet(Type... variableTypes) {
      Pair<String, Type>[] pairs = new Pair[variableTypes.length];
      for (int i = 0; i < variableTypes.length; i++) {
         pairs[i] = new Pair<>(null, variableTypes[i]);
      }
      return new VariableSet(pairs);
   }

   /** Constructs a variable set containing variables of the specified types. */
   public static VariableSet createVariableSet(Pair<String, Type>[] variableTypes) { // TODO replace array with list
      return new VariableSet(variableTypes);
   }

   private VariableSet(Pair<String, Type>[] variableTypes) {
      // TODO validate variable names are valid and unique
      this.variables = new VariableNode[variableTypes.length];
      for (int i = 0; i < variableTypes.length; i++) {
         this.variables[i] = new VariableNode(i, variableTypes[i].getKey(), variableTypes[i].getValue());
      }
      this.variablesByType = new TypeMap<>(Arrays.asList(variables), VariableNode::getType);
   }

   private VariableSet(VariableNode[] variables) { // TODO make this the only constructor
      this.variables = variables;
      this.variablesByType = new TypeMap<>(Arrays.asList(variables), VariableNode::getType);
   }

   /**
    * Returns a list of all variables in this set that are of the specified type.
    *
    * @param type
    *           the type to find matching variables of
    * @return a list of all variables in this set that are the specified type, or an empty list if there are no variables of the required type in this set
    */
   public List<VariableNode> getByType(Type type) {
      return variablesByType.getByType(type);
   }

   /** Returns the {@code VariableNode} from this set that is associated with the specified ID. */
   public VariableNode getById(int id) {
      return variables[id];
   }

   public VariableNode getByName(String token) { // TODO comment and test
      for (VariableNode v : variables) {
         if (v.toString().equals(token)) {
            return v;
         }
      }
      return null;
   }

   public int size() {
      return variables.length;
   }
}
