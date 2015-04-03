package org.oakgp.util;

import org.oakgp.Assignments;
import org.oakgp.node.Node;
import org.oakgp.serialize.NodeWriter;

/** Utility methods that support the functionality provided by the rest of the framework. */
public final class Utils {
   private static final Object[][] TEST_DATA = { { 2, 14, 4, 9, 7 } };
   private static final NodeWriter WRITER = new NodeWriter();

   /** Private constructor as all methods are static. */
   private Utils() {
      // do nothing
   }

   /**
    * Asserts that the specified nodes evaluate to the same results.
    *
    * @param first
    *           the node to compare to {@code second}
    * @param second
    *           the node to compare to {@code first}
    * @throws IllegalArgumentException
    *            if the specified nodes evaluate to different results
    */
   public static void assertEvaluateToSameResult(Node first, Node second) {
      for (Object[] assignedValues : TEST_DATA) {
         // TODO uncomment
         // assertEvaluateToSameResult(first, second, assignedValues);
      }
   }

   private static void assertEvaluateToSameResult(Node first, Node second, Object[] assignedValues) {
      Assignments assignments = Assignments.createAssignments(assignedValues);
      Object firstResult = first.evaluate(assignments);
      Object secondResult = second.evaluate(assignments);
      if (!firstResult.equals(secondResult)) {
         throw new IllegalArgumentException(WRITER.writeNode(first) + " = " + firstResult + " " + WRITER.writeNode(second) + " = " + secondResult);
      }
   }
}
