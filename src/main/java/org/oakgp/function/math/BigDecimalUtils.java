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

import static org.oakgp.type.CommonTypes.bigDecimalType;

import java.math.BigDecimal;

/** Provides support for working with instances of {@code java.math.BigDecimal}. */
public final class BigDecimalUtils extends NumberUtils<BigDecimal> {
   /** Singleton instance. */
   public static final BigDecimalUtils BIG_DECIMAL_UTILS = new BigDecimalUtils();

   /** @see #BIG_DECIMAL_UTILS */
   private BigDecimalUtils() {
      super(bigDecimalType(), BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.valueOf(2));
   }

   @Override
   protected BigDecimal add(BigDecimal i1, BigDecimal i2) {
      return i1.add(i2);
   }

   @Override
   protected BigDecimal subtract(BigDecimal i1, BigDecimal i2) {
      return i1.subtract(i2);
   }

   @Override
   protected BigDecimal multiply(BigDecimal i1, BigDecimal i2) {
      return i1.multiply(i2);
   }

   @Override
   protected BigDecimal divide(BigDecimal i1, BigDecimal i2) {
      return i1.divide(i2);
   }
}
