package org.oakgp.util;

/** Allows a {@code java.util.Random} to be used as a {@code org.oakgp.util.Random}. */
public final class JavaUtilRandomAdapter implements Random {
   private final java.util.Random random;

   public JavaUtilRandomAdapter() {
      this.random = new java.util.Random();
   }

   /**
    * @param seed
    *           the seed to specify in the constructor of the {@code java.util.Random} this object wraps
    */
   public JavaUtilRandomAdapter(long seed) {
      this.random = new java.util.Random(seed);
   }

   @Override
   public int nextInt(int bound) {
      return random.nextInt(bound);
   }

   @Override
   public double nextDouble() {
      return random.nextDouble();
   }

   @Override
   public boolean nextBoolean() {
      return random.nextBoolean();
   }
}
