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
import org.oakgp.util.Utils;

/**
 * Represents the type signature of a {@code Function}.
 * <p>
 * A signature includes the return type, the number of arguments and the type of each argument.
 */
public final class Signature {
   private final Type returnType;
   private final Type[] argumentTypes;
   private final int hashCode;

   /** Returns a new {@code Signature} with the specified return type and argument types. */
   public static Signature createSignature(Type returnType, Type... argumentTypes) {
      return new Signature(returnType, argumentTypes);
   }

   private Signature(Type returnType, Type[] userSuppliedArgumentTypes) {
      this.returnType = returnType;
      this.argumentTypes = Utils.copyOf(userSuppliedArgumentTypes);
      this.hashCode = (returnType.hashCode() * 31) * Arrays.hashCode(argumentTypes);
   }

   /** Returns the type associated with values returned by the evaluation of functions that have this signature. */
   public Type getReturnType() {
      return returnType;
   }

   /**
    * Returns the {@code Type} at the specified position in the arguments of this signature.
    *
    * @param index
    *           index of the argument to return
    * @return the {@code Type} at the specified position in the arguments of this signature.
    * @throws ArrayIndexOutOfBoundsException
    *            if the index is out of range (<tt>index &lt; 0 || index &gt;= getArgumentTypesLength()</tt>)
    */
   public Type getArgumentType(int index) {
      return argumentTypes[index];
   }

   /** Returns the number of arguments in this signature. */
   public int getArgumentTypesLength() {
      return argumentTypes.length;
   }

   /** Returns an unmodifiable list containing the type of each argument associated with this signature. */
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
