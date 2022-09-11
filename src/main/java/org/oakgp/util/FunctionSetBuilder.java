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
package org.oakgp.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.type.Types.Type;

public final class FunctionSetBuilder {
   private final List<FunctionSet.Key> functions = new ArrayList<>();

   public FunctionSetBuilder addAll(Function... values) {
      for (Function f : values) {
         add(f);
      }
      return this;
   }

   public FunctionSetBuilder add(Function function, Type... parameters) {
      return put(function, function.getSignature().create(parameters));
   }

   public FunctionSetBuilder add(Function function) {
      return put(function, function.getSignature());
   }

   public FunctionSetBuilder add(Class<?> target, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
      return add(target.getMethod(methodName, parameterTypes));
   }

   public FunctionSetBuilder add(Method method) {
      Function f = MethodFunction.createFunction(method);
      return put(f, f.getSignature());
   }

   public FunctionSetBuilder put(Function function, Signature signature) {
      // TODO check for duplicates
      // TODO rename from put to add?
      functions.add(new FunctionSet.Key(function, signature));
      return this;
   }

   public FunctionSet build() {
      return new FunctionSet(functions);
   }
}
