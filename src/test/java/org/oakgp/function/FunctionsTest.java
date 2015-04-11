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
