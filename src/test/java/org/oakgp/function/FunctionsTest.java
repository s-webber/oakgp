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

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.type.Types.Type;

public class FunctionsTest {
   private static final Type[] EMPTY = new Type[0];

   /** Confirms that each implementation of {@code Function} has exactly one corresponding {@code AbstractFunctionTest} instance. */
   @Test
   public void test() {
      List<Class<?>> functionClasses = SubClassFinder.find(Function.class, "src/main/java");
      List<Function> testFunctions = getTestFunctions();
      for (Function f : testFunctions) {
         if (!functionClasses.remove(f.getClass())) {
            throw new RuntimeException("No record of: " + f.getClass());
         }
      }
      assertTrue("Not tested " + functionClasses.toString(), functionClasses.isEmpty());
   }

   public static List<Function> getTestFunctions() {
      List<Function> result = new ArrayList<>();

      List<Class<?>> functionTestClasses = SubClassFinder.find(AbstractFunctionTest.class, "src/test/java");
      for (Class<?> functionTest : functionTestClasses) {
         AbstractFunctionTest t = (AbstractFunctionTest) newInstance(functionTest);
         if (!result.add(t.getFunction())) {
            throw new RuntimeException("Tested more than once: " + t.getFunction());
         }
      }

      return result;
   }

   @SuppressWarnings("unchecked")
   private static <T> T newInstance(Class<?> functionTest) {
      try {
         return (T) functionTest.newInstance();
      } catch (ReflectiveOperationException e) {
         throw new RuntimeException(e);
      }
   }

   // TODO move to separate class
   public static FunctionSet autowire(List<Function> functions, Set<Type> inputTypes) {
      List<FunctionSet.Key> allKeys = new ArrayList<>();

      for (Function f : functions) {
         allKeys.addAll(getKeys(f, inputTypes));
      }

      return new FunctionSet(allKeys);
   }

   private static List<FunctionSet.Key> getKeys(Function f, Set<Type> allTypes) {
      List<FunctionSet.Key> result = new ArrayList<>();

      Signature signature = f.getSignature();
      Set<Type[]> combinations = getTypes(signature, allTypes);
      for (Type[] types : combinations) {
         try {
            FunctionSet.Key key = new FunctionSet.Key(f, signature.create(types));
            result.add(key);
         } catch (IllegalArgumentException e) {
         }
      }

      return result;
   }

   private static Set<Type[]> getTypes(Signature signature, Set<Type> allTypes) {
      if (signature.isTemplate()) {
         return recursivelyGetTypes(allTypes, EMPTY, signature.getGenerics().length);
      } else {
         return Collections.singleton(EMPTY);
      }
   }

   private static Set<Type[]> recursivelyGetTypes(Set<Type> allTypes, Type[] currentCombination, int maxDepth) {
      if (currentCombination.length == maxDepth) {
         return Collections.singleton(currentCombination);
      }

      Set<Type[]> result = new HashSet<>();
      for (Type type : allTypes) {
         Type[] next = Arrays.copyOf(currentCombination, currentCombination.length + 1);
         next[currentCombination.length] = type;
         result.addAll(recursivelyGetTypes(allTypes, next, maxDepth));
      }
      return result;
   }
}
