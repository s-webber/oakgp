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
package org.oakgp.node;

import org.oakgp.Assignments;
import org.oakgp.Type;

/**
 * Represents a variable.
 * <p>
 * The result of evaluating this node will vary based on the {@link Assignments} specified.
 */
public final class VariableNode extends TerminalNode {
   private final int id;
   private final Type type;
   private final int hashCode;

   /**
    * Constructs a new {@code VariableNode} with the specified ID.
    *
    * @param id
    *           represents the index to specify when getting the value for this variable from an {@link Assignments}
    * @param type
    *           the {@code Type} that the values represented by this node are of
    */
   public VariableNode(int id, Type type) {
      this.id = id;
      this.type = type;
      // +1 so never multiplying by 0
      // *997 as an alternative to *31 - so VariableNode with id 1 has a different hash code than a ConstantNode with an Integer with value 1
      this.hashCode = (id + 1) * 997;
   }

   public int getId() {
      return id;
   }

   /**
    * Returns the value assigned to this {@code VariableNode} by the specified {@code Assignments}.
    *
    * @return the value stored in {@code Assignments} at the index specified by the ID of this {@code VariableNode}
    */
   @Override
   public Object evaluate(Assignments assignments) {
      return assignments.get(id);
   }

   @Override
   public Type getType() {
      return type;
   }

   @Override
   public NodeType getNodeType() {
      return NodeType.VARIABLE;
   }

   @Override
   public int hashCode() {
      return hashCode;
   }

   /** Two VariableNode references are only "equal" if they refer to the same instance. */
   @Override
   public boolean equals(Object o) {
      // Even though this implementation is the same as Object.equals(Object),
      // still overriding in order to be explicit about the behaviour and to be able to add javadoc.
      return this == o;
   }

   @Override
   public String toString() {
      return "v" + id;
   }
}
