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

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class FunctionsTest {
   /** Confirms that each implementation of {@code Function} has exactly one corresponding {@code AbstractFunctionTest} instance. */
   @Test
   public void test() {
      List<Class<?>> functionClasses = SubClassFinder.find(Function.class, "src/main/java");
      List<Function> testFunctions = getTestFunctions();
      for (Function f : testFunctions) {
         if (!functionClasses.remove(f.getClass())) {
            throw new RuntimeException("No record of: " + f.getClass());
         }
         if (!Modifier.isFinal(f.getClass().getModifiers())) {
            throw new RuntimeException("Not final: " + f.getClass());
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
}
