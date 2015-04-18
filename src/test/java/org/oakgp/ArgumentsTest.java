package org.oakgp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.TestUtils.integerConstant;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.oakgp.node.Node;

public class ArgumentsTest {
   @Test
   public void testCreateArguments() {
      Node x = integerConstant(1);
      Node y = integerConstant(2);
      Node z = integerConstant(3);
      Node[] args = { x, y, z };
      Arguments first = Arguments.createArguments(args);
      assertArguments(first, x, y, z);

      Node a = integerConstant(4);
      args[1] = a;
      Arguments second = Arguments.createArguments(args);
      assertArguments(second, x, a, z);

      // assert the Arguments created first remains unchanged by subsequent changes to args
      assertArguments(first, x, y, z);
   }

   @Test
   public void testCreateArgumentsFromList() {
      Node x = integerConstant(1);
      Node y = integerConstant(2);
      Node z = integerConstant(3);
      Node[] array = { x, y, z };
      List<Node> list = Arrays.asList(array);
      Arguments first = Arguments.createArguments(array);
      Arguments second = Arguments.createArguments(list);
      assertEquals(first, second);
   }

   @Test
   public void testReplaceAt() {
      // create arguments
      Node x = integerConstant(1);
      Node y = integerConstant(2);
      Node z = integerConstant(3);
      Arguments original = Arguments.createArguments(x, y, z);
      assertArguments(original, x, y, z);

      // create new arguments based on original
      Node replacement = integerConstant(9);
      assertArguments(original.replaceAt(0, replacement), replacement, y, z);
      assertArguments(original.replaceAt(1, replacement), x, replacement, z);
      assertArguments(original.replaceAt(2, replacement), x, y, replacement);

      // assert original arguments has remained unchanged
      assertArguments(original, x, y, z);
   }

   @Test
   public void testArrayIndexOutOfBoundsException() {
      Arguments arguments = Arguments.createArguments(integerConstant(7), integerConstant(42));
      assertArrayIndexOutOfBoundsException(arguments, -1);
      assertArrayIndexOutOfBoundsException(arguments, 2);
   }

   @Test
   public void testEqualsAndHashCode() {
      Arguments a1 = Arguments.createArguments(integerConstant(7), createVariable(0), integerConstant(42));
      Arguments a2 = Arguments.createArguments(integerConstant(7), createVariable(0), integerConstant(42));
      assertEquals(a1, a1);
      assertEquals(a1.hashCode(), a2.hashCode());
      assertEquals(a1, a2);
   }

   @Test
   public void testNotEquals() {
      Arguments a = Arguments.createArguments(integerConstant(7), createVariable(0), integerConstant(42));

      // same arguments, different order
      assertNotEquals(a, Arguments.createArguments(integerConstant(42), createVariable(0), integerConstant(7)));

      // different arguments
      assertNotEquals(a, Arguments.createArguments(integerConstant(7), createVariable(0), integerConstant(43)));

      // one fewer argument
      assertNotEquals(a, Arguments.createArguments(integerConstant(7), createVariable(0)));

      // one extra argument
      assertNotEquals(a, Arguments.createArguments(integerConstant(7), createVariable(0), integerConstant(42), integerConstant(42)));
   }

   @Test
   public void testToString() {
      Arguments arguments = Arguments.createArguments(integerConstant(7), createVariable(0), integerConstant(42));
      assertEquals("[7, v0, 42]", arguments.toString());
   }

   private void assertArrayIndexOutOfBoundsException(Arguments arguments, int index) {
      try {
         arguments.getArg(index);
         fail();
      } catch (ArrayIndexOutOfBoundsException e) {
         // expected
      }
   }

   /**
    * tests {@link Arguments#getArgCount()}, {@link Arguments#getArg(int)}, {@link Arguments#firstArg()}, {@link Arguments#secondArg()},
    * {@link Arguments#thirdArg()}
    */
   private void assertArguments(Arguments actual, Node... expected) {
      assertEquals(expected.length, actual.getArgCount());
      assertSame(expected[0], actual.firstArg());
      assertSame(expected[1], actual.secondArg());
      assertSame(expected[2], actual.thirdArg());
      for (int i = 0; i < expected.length; i++) {
         assertSame(expected[i], actual.getArg(i));
      }
   }
}
