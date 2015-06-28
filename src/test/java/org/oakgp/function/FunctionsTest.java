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

import java.util.List;

import org.junit.Test;

public class FunctionsTest {
   /** Confirms that each implementation of {@code Function} has exactly one corresponding {@code AbstractFunctionTest} instance. */
   @Test
   public void test() throws Exception {
      List<Class<?>> functionClasses = SubClassFinder.find(Function.class, "src/main/java");
      List<Class<?>> functionTestClasses = SubClassFinder.find(AbstractFunctionTest.class, "src/test/java");
      for (Class<?> functionTest : functionTestClasses) {
         AbstractFunctionTest t = (AbstractFunctionTest) functionTest.newInstance();
         Class<?> functionClass = t.getFunction().getClass();
         assertTrue("Tested more than once: " + functionClass, functionClasses.contains(functionClass));
         functionClasses.remove(functionClass);
      }
      assertTrue("Not tested " + functionClasses.toString(), functionClasses.isEmpty());
   }
}
