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
package org.oakgp.util;

/** Allows a {@code java.util.Random} to be used as a {@code org.oakgp.util.Random}. */
public final class JavaUtilRandomAdapter implements Random {
   private final java.util.Random random;

   /**
    * Creates a new random number generator.
    * <p>
    * Uses the no-arg {@code java.util.Random} constructor which sets its seed to a value very likely to be distinct from any other invocation of the
    * constructor.
    */
   public JavaUtilRandomAdapter() {
      this.random = new java.util.Random();
   }

   /**
    * Creates a new random number generator using the specified seed.
    *
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
