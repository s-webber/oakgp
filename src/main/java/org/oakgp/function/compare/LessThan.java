package org.oakgp.operator.compare;

public final class LessThan extends ComparisonOperator {
   public LessThan() {
      super(false);
   }

   @Override
   protected boolean evaluate(int arg1, int arg2) {
      return arg1 < arg2;
   }
}
