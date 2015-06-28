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

import org.oakgp.Type;

/** Determines if the object represented by the first argument is less than or equal to the object represented by the second. */
public final class LessThanOrEqual extends ComparisonOperator {
   public LessThanOrEqual(Type type) {
      super(type, true);
   }

   @Override
   protected boolean evaluate(int diff) {
      return diff <= 0;
   }

   @Override
   public String getDisplayName() {
      return "<=";
   }
}
