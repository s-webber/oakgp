/*
 * Copyright 2019 S. Webber
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

import static org.oakgp.util.Utils.copyOf;

import java.util.Arrays;
import java.util.List;

/**
 * Represents the children of a {@code FunctionNode}.
 * <p>
 * Immutable.
 */
public final class ChildNodes {
   private final Node[] args;
   private final int hashCode;

   /**
    * Returns a new {@code ChildNodes} which contains the specified values.
    * <p>
    * Note: {@code ChildNodes} is immutable - so subsequent changes to {@code args} will not be reflected in the returned {@code ChildNodes}.
    *
    * @param args
    *           the values to be stored in the {@code ChildNodes}
    * @return a new {@code ChildNodes} which contains the values specified by {@code args}
    */
   public static ChildNodes createChildNodes(List<? extends Node> args) {
      return new ChildNodes(args.toArray(new Node[args.size()]));
   }

   /**
    * Returns a new {@code ChildNodes} which contains the specified values.
    * <p>
    * Note: {@code ChildNodes} is immutable - so subsequent changes to {@code args} will not be reflected in the returned {@code ChildNodes}.
    *
    * @param args
    *           the values to be stored in the {@code ChildNodes}
    * @return a new {@code ChildNodes} which contains the values specified by {@code args}
    */
   public static ChildNodes createChildNodes(Node... args) {
      return new ChildNodes(copyOf(args));
   }

   /** @see #createChildNodes(Node...) */
   private ChildNodes(Node[] args) {
      this.args = args;
      this.hashCode = Arrays.hashCode(args);
   }

   /**
    * Returns the {@code Node} at the specified position in this {@code ChildNodes}.
    *
    * @param index
    *           index of the element to return
    * @return the {@code Node} at the specified position in this {@code ChildNodes}
    * @throws ArrayIndexOutOfBoundsException
    *            if the index is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>)
    */
   public Node getNode(int index) {
      return args[index];
   }

   /** Returns the first {@code Node} of this {@code ChildNodes}. */
   public Node first() {
      return args[0];
   }

   /** Returns the second {@code Node} of this {@code ChildNodes}. */
   public Node second() {
      return args[1];
   }

   /** Returns the third {@code Node} of this {@code ChildNodes}. */
   public Node third() {
      return args[2];
   }

   /**
    * Returns the number of elements in this {@code ChildNodes}.
    *
    * @return the number of elements in this {@code ChildNodes}
    */
   public int size() {
      return args.length;
   }

   /**
    * Returns a new {@code ChildNodes} resulting from replacing the existing {@code Node} at position {@code index} with {@code replacement}.
    *
    * @param index
    *           the index of the {@code Node} that needs to be replaced.
    * @param replacement
    *           the new {@code Node} that needs to be store.
    * @return A new {@code ChildNodes} derived from this {@code ChildNodes} by replacing the element at position {@code index} with {@code replacement}.
    */
   public ChildNodes replaceAt(int index, Node replacement) {
      Node[] clone = copyOf(args);
      clone[index] = replacement;
      return new ChildNodes(clone);
   }

   /**
    * Returns a new {@code ChildNodes} resulting from switching the node located at index {@code index1} with the node located at index {@code index2}.
    *
    * @param index1
    *           the index in this {@code ChildNodes} of the first {@code Node} to be swapped.
    * @param index2
    *           the index in this {@code ChildNodes} of the second {@code Node} to be swapped.
    * @return A new {@code ChildNodes} resulting from switching the node located at index {@code index1} with the node located at index {@code index2}.
    */
   public ChildNodes swap(int index1, int index2) {
      Node[] clone = copyOf(args);
      clone[index1] = args[index2];
      clone[index2] = args[index1];
      return new ChildNodes(clone);
   }

   @Override
   public int hashCode() {
      return hashCode;
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof ChildNodes && Arrays.equals(this.args, ((ChildNodes) o).args);
   }

   @Override
   public String toString() {
      return Arrays.toString(args);
   }
}
