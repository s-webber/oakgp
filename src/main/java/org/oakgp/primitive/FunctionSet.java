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

import static org.oakgp.util.Utils.groupBy;

import java.util.List;
import java.util.Map;

import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.type.Types.Type;

/** Represents the set of possible {@code Function} implementations to use during a genetic programming run. */
public final class FunctionSet {
   private final Map<Type, List<Function>> functionsByType;
   private final Map<Signature, List<Function>> functionsBySignature;

   /** Constructs a function set containing the specified functions. */
   public FunctionSet(Function... functions) {
      // TODO validate display names using NodeReader.isValidDisplayName
      this.functionsByType = groupBy(functions, f -> f.getSignature().getReturnType());
      this.functionsBySignature = groupBy(functions, Function::getSignature);
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
}
