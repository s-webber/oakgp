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

import java.util.Map;
import java.util.Objects;

//TODO move to utils package?
public final class Pair<K, V> implements Map.Entry<K, V> {
   private final K key;
   private final V value;

   public Pair(K key, V value) {
      this.key = key;
      this.value = value;
   }

   public K first() {
      return key;
   }

   public V second() {
      return value;
   }

   @Override
   public K getKey() {
      return key;
   }

   @Override
   public V getValue() {
      return value;
   }

   @Override
   public V setValue(V value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public int hashCode() {
      return ((key == null ? -1 : key.hashCode()) * 31) + ((value == null ? -1 : value.hashCode()) * 37);
   }

   @Override
   public boolean equals(Object o) {
      if (o instanceof Pair) {
         Pair p = (Pair) o;
         return Objects.equals(key, p.key) && Objects.equals(value, p.value);
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return "Pair [key=" + key + ", value=" + value + "]";
   }
}
