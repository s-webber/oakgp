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
package org.oakgp.function.compare;

import java.util.concurrent.ConcurrentHashMap;

import org.oakgp.type.Types.Type;

/** Determines if the object represented by the first argument is less than the object represented by the second. */
public final class LessThan extends ComparisonOperator {
   private static final ConcurrentHashMap<Type, LessThan> CACHE = new ConcurrentHashMap<>();

   /**
    * Returns a {@code LessThan} for comparing functions of the specified type.
    * <p>
    * If this is the first call to {@code #create(Type)} with the specified {@code Type} then a new instance will be created and returned. If there has
    * previously been calls to {@code #create(Type)} for the specified {@code Type} then the existing instance will be returned.
    */
   public static LessThan create(Type t) {
      return CACHE.computeIfAbsent(t, LessThan::new);
   }

   /** Constructs a function that compares two arguments of the specified type. */
   private LessThan(Type type) {
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
