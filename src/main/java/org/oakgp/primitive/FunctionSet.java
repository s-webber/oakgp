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

import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;

import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.type.Types.Type;

/** Represents the set of possible {@code Function} implementations to use during a genetic programming run. */
public final class FunctionSet {
   private static final BinaryOperator<List<Function>> MERGE_FUNCTION = (x, y) -> {
      ArrayList<Function> a = new ArrayList<>();
      a.addAll(x);
      a.addAll(y);
      return unmodifiableList(a);
   };

   private final List<Key> functions;
   private final Map<Signature, List<Function>> functionsBySignature;
   private final Map<Type, List<Function>> functionsByType;

   /** Constructs a function set containing the specified functions. */
   public FunctionSet(List<FunctionSet.Key> functions) {
      // TODO validate display names using NodeReader.isValidDisplayName
      this.functions = unmodifiableList(new ArrayList<>(functions)); // TODO add immutableCopy(List) method to Utils
      this.functionsBySignature = functions.stream().collect(toMultiValueMap(k -> k.getSignature(), k -> singletonList(k.getFunction())));
      this.functionsByType = functionsBySignature.entrySet().stream().collect(toMultiValueMap(e -> e.getKey().getReturnType(), Map.Entry::getValue));
   }

   private <I, K> Collector<I, ?, LinkedHashMap<K, List<Function>>> toMultiValueMap(java.util.function.Function<I, K> keyMapper,
         java.util.function.Function<I, List<Function>> valueMapper) {
      return toMap(keyMapper, valueMapper, MERGE_FUNCTION, LinkedHashMap::new);
   }

   /**
    * Returns a list of all functions in this set that have the specified return type.
    *
    * @param type
    *           the type to find matching functions of
    * @return a list of all functions in this set that have the specified return type, or {@code null} if there are no functions with the required return type
    *         in this set
    */
   public List<Function> getByType(Type type) {
      // TODO should this return an empty list, rather than null, if no match found?
      return functionsByType.get(type);
   }

   /**
    * Returns a list of all functions in this set that have the specified signature.
    *
    * @param signature
    *           the signature to find matching functions of
    * @return a list of all functions in this set that have the specified signature, or {@code null} if there are no functions with the required signature in
    *         this set
    */
   public List<Function> getBySignature(Signature signature) {
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

   public static class Key {
      private final Function function;
      private final Signature signature;

      public Key(Function function, Signature signature) {
         if (signature.isTemplate()) {
            throw new IllegalArgumentException(signature.toString());
         }
         this.function = function;
         this.signature = signature;
      }

      public Function getFunction() {
         return function;
      }

      public Signature getSignature() {
         return signature;
      }
   }
}
