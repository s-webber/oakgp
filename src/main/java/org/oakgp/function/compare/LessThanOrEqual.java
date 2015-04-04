package org.oakgp.function.compare;

/** Determines if the number represented by the first argument is less than or equal to the number represented by the second. */
public final class LessThanOrEqual extends ComparisonOperator {
   public LessThanOrEqual() {
      super(true);
   }

   @Override
   protected boolean evaluate(int arg1, int arg2) {
      return arg1 <= arg2;
   }
}
