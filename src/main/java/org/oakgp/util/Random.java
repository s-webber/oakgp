package org.oakgp.util;

/**
 * Represents a random number generator.
 * <p>
 * This interface is used by the framework (rather than directly using the {@code java.util.Random} concrete class) in order to provide support for different
 * approaches to generating random numbers. e.g. There may be a requirement to use a hardware random number generator.
 */
public interface Random {
   /**
    * Returns a {@code int} value between 0 (inclusive) and the specified value (exclusive)
    *
    * @param bound
    *           the upper bound (exclusive)
    * @return a {@code int} value between 0 (inclusive) and the specified value (exclusive)
    */
   int nextInt(int bound);

   /**
    * Returns a {@code double} value in the range {@code 0.0d} (inclusive) to {@code 1.0d} (exclusive).
    *
    * @return a {@code double} value in the range {@code 0.0d} (inclusive) to {@code 1.0d} (exclusive)
    */
   double nextDouble();

   /**
    * Returns a {@code boolean} value.
    *
    * @return a {@code boolean} value
    */
   boolean nextBoolean();
}
