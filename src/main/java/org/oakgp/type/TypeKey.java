/*
 * Copyright 2019 S. Webber
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
package org.oakgp.type;

import java.util.Arrays;

import org.oakgp.type.Types.Type;

/** Used as keys for the caching of {@link Types.Type} instances. */
final class TypeKey {
   private final String name;
   private final Type[] parameters;
   private final int hashCode;

   TypeKey(String name, Type... parameters) {
      this.name = name;
      this.parameters = Arrays.copyOf(parameters, parameters.length);
      this.hashCode = (name.hashCode() * 31) * Arrays.hashCode(this.parameters);
   }

   @Override
   public boolean equals(Object o) {
      TypeKey k = (TypeKey) o;
      if (this.name.equals(k.name) && Arrays.equals(this.parameters, k.parameters)) {
         if (this.hashCode != k.hashCode) {
            throw new RuntimeException(o + " " + this);
         }
      }
      return this.name.equals(k.name) && Arrays.equals(this.parameters, k.parameters);
   }

   @Override
   public int hashCode() {
      return hashCode;
   }

   @Override
   public String toString() {
      if (parameters.length == 0) {
         return name;
      } else {
         return name + " " + Arrays.toString(parameters);
      }
   }
}
