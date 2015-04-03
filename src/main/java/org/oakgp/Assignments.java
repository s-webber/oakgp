package org.oakgp;

import java.util.Arrays;

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
    * Note: {@code Assignments} is immutable - so subsequent changes to {@code values} will not be reflected in the returned {@code Assignments}.
    *
    * @param values
    *           the values to be stored in the {@code Assignments}
    * @return a new {@code Assignments} which contains the values specified by {@code values}
    */
   public static Assignments createAssignments(Object... values) {
      Object[] copy = new Object[values.length];
      System.arraycopy(values, 0, copy, 0, values.length);
      return new Assignments(copy);
   }

   /** @see #createAssignments(Node...) */
   private Assignments(Object[] assignments) {
      this.assignments = assignments;
      this.hashCode = Arrays.hashCode(assignments);
   }

   /**
    * Returns the value at the specified position in this {@code Assignments}.
    *
    * @param index
    *           index of the element to return
    * @return the value at the specified position in this {@code Assignments}
    * @throws IndexOutOfBoundsException
    *            if the index is out of range
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
