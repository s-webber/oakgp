package org.oakgp.function.compare;

import org.oakgp.Type;

/** Determines if the number represented by the first argument is less than the number represented by the second. */
public final class LessThan extends ComparisonOperator {
   public LessThan(Type type) {
      super(type, false);
   }

   @Override
   protected boolean evaluate(int diff) {
      return diff < 0;
   }

   @Override
   public String getDisplayName() {
      return "<";
   }
}
