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
package org.oakgp;

import java.util.Arrays;

import org.oakgp.util.Utils;

/**
 * Represents values assigned to variables.
 * <p>
 * Immutable.
 */
public final class Assignments {
   private final Object[] assignments;
   private final int hashCode;

   /**
    * Returns a new {@code Assignments} which contains the specified values.
    * <p>
    * Note: {@code Assignments} is immutable - so subsequent changes to {@code values} will not be reflected in the
    * returned {@code Assignments}.
    *
    * @param values the values to be stored in the {@code Assignments}
    * @return a new {@code Assignments} which contains the values specified by {@code values}
    */
   public static Assignments createAssignments(Object... values) {
      if (values.length == 1 && values[0] instanceof Object[]) { // TODO
         throw new IllegalArgumentException();
      }
      return new Assignments(values);
   }

   /** @see #createAssignments(Node...) */
   private Assignments(Object[] assignments) {
      this.assignments = Utils.copyOf(assignments);
      this.hashCode = Arrays.hashCode(assignments);
   }

   /**
    * Returns the value at the specified position in this {@code Assignments}.
    *
    * @param index index of the element to return
    * @return the value at the specified position in this {@code Assignments}
    * @throws IndexOutOfBoundsException if the index is out of range
    */
   public Object get(int index) {
      return assignments[index];
   }

   @Override
   public int hashCode() {
      return hashCode;
   }

   @Override
   public boolean equals(Object o) {
      return o instanceof Assignments && Arrays.equals(this.assignments, ((Assignments) o).assignments);
   }

   @Override
   public String toString() {
      return Arrays.toString(assignments);
   }
}
