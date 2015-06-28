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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DummyValuesMap<K, V> {
   private final Map<K, DummyValuesQueue<V>> map;

   public DummyValuesMap(K key, V... values) {
      map = new HashMap<>();
      map.put(key, new DummyValuesQueue<>(values));
   }

   private DummyValuesMap(Map<K, DummyValuesQueue<V>> map) {
      this.map = new HashMap<>(map);
   }

   public V next(K key) {
      DummyValuesQueue<V> values = map.get(key);
      if (values == null) {
         throw new IllegalArgumentException("No entry found for key: " + key + " of type: " + key.getClass().getName() + " only: " + map.keySet());
      } else if (values.isEmpty()) {
         throw new IllegalArgumentException("No remaining values found for key: " + key + " of type: " + key.getClass().getName());
      } else {
         return values.next();
      }
   }

   public static class Builder<K, V> {
      private final Map<K, DummyValuesQueue<V>> map = new HashMap<>();

      public Builder<K, V> put(K key, V... values) {
         return put(key, new DummyValuesQueue<>(values));
      }

      public Builder<K, V> put(K key, DummyValuesQueue<V> values) {
         if (map.containsKey(key)) {
            throw new IllegalStateException("Values already exist for key: " + key);
         } else {
            map.put(key, values);
         }
         return this;
      }

      public DummyValuesMap<K, V> build() {
         return new DummyValuesMap<>(map);
      }
   }

   public void assertEmpty() {
      for (Entry<K, DummyValuesQueue<V>> e : map.entrySet()) {
         if (!e.getValue().isEmpty()) {
            throw new IllegalStateException("There are still unselected values for key: " + e.getKey());
         }
      }
   }
}
