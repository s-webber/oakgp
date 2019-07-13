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

import org.oakgp.type.Types.Type;

/** Provides a convenient way for {@link CommonTypes} to define types. */
class TypeBuilder {
   private final String name;
   private Type[] parents = new Type[0];
   private Type[] parameters = new Type[0];

   static TypeBuilder name(Class<?> typeClass) {
      return name(typeClass.getSimpleName());
   }

   static TypeBuilder name(String name) {
      return new TypeBuilder(name);
   }

   private TypeBuilder(String name) {
      this.name = name;
   }

   TypeBuilder parents(Type... parents) {
      this.parents = parents;
      return this;
   }

   TypeBuilder parameters(Type... parameters) {
      this.parameters = parameters;
      return this;
   }

   Type build() {
      return Types.declareType(name, parents, parameters);
   }
}
