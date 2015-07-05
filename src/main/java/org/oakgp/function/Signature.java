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
package org.oakgp.function;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.oakgp.Type;

/**
 * Represents the type signature of a {@code Function}.
 * <p>
 * A signature includes the return type, the number of arguments and the type of each argument.
 */
public final class Signature {
   private final Type returnType;
   private final Type[] argumentTypes;
   private final int hashCode;

   public static Signature createSignature(Type returnType, Type... argumentTypes) {
      Type[] copy = new Type[argumentTypes.length];
      System.arraycopy(argumentTypes, 0, copy, 0, argumentTypes.length);
      return new Signature(returnType, copy);
   }

   private Signature(Type returnType, Type[] argumentTypes) {
      this.returnType = returnType;
      this.argumentTypes = argumentTypes;
      this.hashCode = (returnType.hashCode() * 31) * Arrays.hashCode(argumentTypes);
   }

   public Type getReturnType() {
      return returnType;
   }

   public Type getArgumentType(int index) {
      return argumentTypes[index];
   }

   public int getArgumentTypesLength() { // TODO rename?
      return argumentTypes.length;
   }

   public List<Type> getArgumentTypes() {
      return Collections.unmodifiableList(Arrays.asList(argumentTypes));
   }

   @Override
   public int hashCode() {
      return hashCode;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o instanceof Signature) {
         Signature s = (Signature) o;
         return this.returnType == s.returnType && Type.sameTypes(this.argumentTypes, s.argumentTypes);
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return returnType + " " + Arrays.toString(argumentTypes);
   }
}
