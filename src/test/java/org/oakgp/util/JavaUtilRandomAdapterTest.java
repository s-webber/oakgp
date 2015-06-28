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
package org.oakgp.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class JavaUtilRandomAdapterTest {
   @Test
   public void test() {
      int seed = 0;
      Random adapter = new JavaUtilRandomAdapter(seed);
      java.util.Random javaUtilRandom = new java.util.Random(seed);

      assertEquals(javaUtilRandom.nextInt(5), adapter.nextInt(5));
      assertEquals(javaUtilRandom.nextInt(1000), adapter.nextInt(1000));
      assertEquals(javaUtilRandom.nextInt(Integer.MAX_VALUE), adapter.nextInt(Integer.MAX_VALUE));

      assertEquals(javaUtilRandom.nextDouble(), adapter.nextDouble(), 0);
      assertEquals(javaUtilRandom.nextDouble(), adapter.nextDouble(), 0);
      assertEquals(javaUtilRandom.nextDouble(), adapter.nextDouble(), 0);

      assertEquals(javaUtilRandom.nextBoolean(), adapter.nextBoolean());
      assertEquals(javaUtilRandom.nextBoolean(), adapter.nextBoolean());
      assertEquals(javaUtilRandom.nextBoolean(), adapter.nextBoolean());
   }
}
