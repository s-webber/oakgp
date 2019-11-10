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
package org.oakgp.function;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.oakgp.primitive.FunctionSet;
import org.oakgp.type.Types.Type;

/**
 * Returns all valid concrete implementations of the given (possibly generic) function using all valid permutations of the given types.
 */
class FunctionKeyFactory { // TODO rename
   /**
    * Returns all valid concrete implementations of the given (possibly generic) function using all valid permutations of the given types.
    */
   static Collection<FunctionSet.Key> createKeys(Function function, Set<Type> allTypes) {
      Signature signature = function.getSignature();
      if (!signature.isTemplate()) {
         return Collections.singleton(new FunctionSet.Key(function, signature));
      }

      List<FunctionSet.Key> result = new ArrayList<>();

      final List<Type>[] validArgumentTypes = getValidArgumentTypes(function, allTypes);
      final int[] current = new int[validArgumentTypes.length];
      Arrays.fill(current, -1);
      int ctr = 0;
      while (ctr > -1) {
         if (ctr == current.length) {
            Type[] genericArguments = new Type[current.length];
            for (int i = 0; i < genericArguments.length; i++) {
               genericArguments[i] = validArgumentTypes[i].get(current[i]);
            }

            FunctionSet.Key key = new FunctionSet.Key(function, signature.create(genericArguments));
            result.add(key);

            ctr--;
         }

         current[ctr]++;
         if (current[ctr] == validArgumentTypes[ctr].size()) {
            current[ctr] = -1;
            ctr--;
         } else {
            ctr++;
         }
      }

      return result;
   }

   /** For each argument of the given function, return all of the given types that are assignable to it. */
   private static List<Type>[] getValidArgumentTypes(Function function, Set<Type> allTypes) {
      Type[] generics = function.getSignature().getGenerics();

      @SuppressWarnings("unchecked")
      final List<Type>[] validArgumentTypes = new List[generics.length];
      for (int i = 0; i < generics.length; i++) {
         Type generic = generics[i];
         validArgumentTypes[i] = allTypes.stream().filter(t -> t.isAssignable(generic)).collect(toList());
      }
      return validArgumentTypes;
   }
}
