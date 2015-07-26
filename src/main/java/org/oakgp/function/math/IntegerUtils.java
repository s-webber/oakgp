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
package org.oakgp.function.math;

import static org.oakgp.Type.integerType;

/** Provides support for working with instances of {@code java.lang.Integer}. */
public final class IntegerUtils extends NumberUtils<Integer> {
   /** Singleton instance. */
   public static final IntegerUtils INTEGER_UTILS = new IntegerUtils();

   /** @see #INTEGER_UTILS */
   private IntegerUtils() {
      super(integerType(), 0, 1, 2);
   }

   @Override
   protected Integer add(Integer i1, Integer i2) {
      return i1 + i2;
   }

   @Override
   protected Integer subtract(Integer i1, Integer i2) {
      return i1 - i2;
   }

   @Override
   protected Integer multiply(Integer i1, Integer i2) {
      return i1 * i2;
   }

   @Override
   protected Integer divide(Integer i1, Integer i2) {
      return i1 / i2;
   }
}
