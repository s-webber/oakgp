package org.oakgp.function.compare;

import org.oakgp.Type;

/** Determines if the object represented by the first argument is less than or equal to the object represented by the second. */
public final class LessThanOrEqual extends ComparisonOperator {
   public LessThanOrEqual(Type type) {
      super(type, true);
   }

   @Override
   protected boolean evaluate(int diff) {
      return diff <= 0;
   }

   @Override
   public String getDisplayName() {
      return "<=";
   }
}
