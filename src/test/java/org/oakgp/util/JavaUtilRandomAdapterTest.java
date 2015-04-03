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
