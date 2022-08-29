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
package org.oakgp.primitive;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.type.Types.Type;
import org.oakgp.util.TypeMap;
import org.oakgp.util.Utils;

/** Represents the set of possible {@code Function} implementations to use during a genetic programming run. */
public final class FunctionSet {
   private final List<Key> functions;
   private final Map<Signature, List<Key>> functionsBySignature;
   private final TypeMap<Key> functionsByType;

   /** Constructs a function set containing the specified functions. */
   public FunctionSet(Collection<FunctionSet.Key> functions) {
      // TODO validate display names using NodeReader.isValidDisplayName
      this.functions = unmodifiableList(new ArrayList<>(functions)); // TODO add immutableCopy(List) method to Utils
      this.functionsBySignature = Utils.groupBy(functions, Key::getSignature);
      this.functionsByType = new TypeMap<>(functions, Key::getReturnType);
   }

   /**
    * Returns a list of all functions in this set that have the specified return type.
    *
    * @param type the type to find matching functions of
    * @return a list of all functions in this set that have the specified return type, or an empty list if there are no
    * functions with the required return type in this set
    */
   public List<Key> getByType(Type type) {
      return functionsByType.getByType(type);
   }

   /**
    * Returns a list of all functions in this set that have the specified signature.
    *
    * @param signature the signature to find matching functions of
    * @return a list of all functions in this set that have the specified signature, or {@code null} if there are no
    * functions with the required signature in this set
    */
   public List<Key> getBySignature(Signature signature) {
      // TODO should this return an empty list, rather than null, if no match found?
      return functionsBySignature.get(signature);
   }

   public List<Key> getFunctions() {
      return functions;
   }

   @Override
   public String toString() {
      return functionsBySignature.toString();
   }

   public final static class Key { // TODO move to separate file and add KeyTest
      private final Function function;
      private final Signature signature;

      public Key(Function function, Signature signature) {
         if (signature.isTemplate()) {
            throw new IllegalArgumentException(function.getDisplayName() + " " + signature.toString());
         }
         this.function = function;
         this.signature = signature;
      }

      public Function getFunction() {
         return function;
      }

      public Type getReturnType() {
         return signature.getReturnType();
      }

      public Signature getSignature() {
         return signature;
      }

      @Override
      public String toString() {
         return "Key [function=" + function.getDisplayName() + ", signature=" + signature + "]";
      }
   }
}
