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
