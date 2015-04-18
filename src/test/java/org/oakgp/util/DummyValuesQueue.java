package org.oakgp.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

public class DummyValuesQueue<T> {
   private Queue<T> values;

   public DummyValuesQueue(T... values) {
      this.values = new LinkedList<>(Arrays.asList(values));
   }

   public T next() throws NoSuchElementException {
      return values.remove();
   }

   public boolean isEmpty() {
      return values.isEmpty();
   }
}
