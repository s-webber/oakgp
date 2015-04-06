package org.oakgp.function.compare;

/** Determines if the number represented by the first argument is less than the number represented by the second. */
public final class LessThan extends ComparisonOperator {
   public LessThan() {
      super(false);
   }

   @Override
   protected boolean evaluate(int arg1, int arg2) {
      return arg1 < arg2;
   }

   @Override
   public String getDisplayName() {
      return "<";
   }
}
