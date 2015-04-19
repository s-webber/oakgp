package org.oakgp.util;

import java.util.HashMap;
import java.util.Map;

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
         throw new IllegalArgumentException("No entry found for key: " + key + " of type: " + key.getClass().getName());
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
}
