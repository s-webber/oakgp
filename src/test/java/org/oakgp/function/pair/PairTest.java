/*
 * Copyright 2022 S. Webber
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
package org.oakgp.function.pair;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PairTest {
   @Test
   public void testGetters() {
      String key = "hello";
      Integer value = new Integer(42);
      Pair p = new Pair(key, value);

      assertSame(key, p.first());
      assertSame(key, p.getKey());
      assertSame(value, p.second());
      assertSame(value, p.getValue());
   }

   @Test
   public void testEquals() {
      String key = "hello";
      Integer value = new Integer(42);
      Pair p = new Pair(key, value);

      assertTrue(p.equals(p));
      assertTrue(p.equals(new Pair(key, value)));
      assertTrue(p.equals(new Pair(new String(key), new Integer(value.intValue()))));
   }

   @Test
   public void testNotEquals() {
      String key = "hello";
      Integer value = new Integer(42);
      Pair p = new Pair(key, value);

      assertFalse(p.equals(new Pair(value, key)));
      assertFalse(p.equals(new Pair(key, value.toString())));
      assertFalse(p.equals(null));
      assertFalse(p.equals(key));
   }

   @Test
   public void testHashCode() {
      String key = "hello";
      Integer value = new Integer(42);
      Pair p = new Pair(key, value);

      assertEquals(p.hashCode(), p.hashCode());
      assertEquals(p.hashCode(), new Pair(key, value).hashCode());
      assertEquals(p.hashCode(), new Pair(new String(key), new Integer(value.intValue())).hashCode());
      assertNotEquals(p.hashCode(), new Pair(key.toUpperCase(), value).hashCode());
      assertNotEquals(p.hashCode(), new Pair(value, key).hashCode());
   }

   // TODO test null values
}