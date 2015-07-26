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

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.unmodifiableList;
import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.oakgp.Type;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;

/** Utility methods that support the functionality provided by the rest of the framework. */
public final class Utils {
   /** Represents the boolean value {@code true}. */
   public static final ConstantNode TRUE_NODE = new ConstantNode(TRUE, booleanType());
   /** Represents the boolean value {@code false}. */
   public static final ConstantNode FALSE_NODE = new ConstantNode(FALSE, booleanType());

   /** Private constructor as all methods are static. */
   private Utils() {
      // do nothing
   }

   /**
    * Returns an array consisting of a {@code ConstantNode} instance for each of the possible values of the specified enum.
    *
    * @param e
    *           the enum that the {@code ConstantNode} instances should wrap
    * @param t
    *           the {@code Type} that should be associated with the {@code ConstantNode} instances
    */
   public static ConstantNode[] createEnumConstants(Class<? extends Enum<?>> e, Type t) {
      Enum<?>[] enumConstants = e.getEnumConstants();
      ConstantNode[] constants = new ConstantNode[enumConstants.length];
      for (int i = 0; i < enumConstants.length; i++) {
         constants[i] = new ConstantNode(enumConstants[i], t);
      }
      return constants;
   }

   /**
    * Returns an array consisting of a {@code ConstantNode} instance for each of the integer values in the specified range.
    *
    * @param minInclusive
    *           the minimum value (inclusive) to be represented by a {@code ConstantNode} in the returned array
    * @param maxInclusive
    *           the minimum value (inclusive) to be represented by a {@code ConstantNode} in the returned array
    */
   public static ConstantNode[] createIntegerConstants(int minInclusive, int maxInclusive) {
      ConstantNode[] constants = new ConstantNode[maxInclusive - minInclusive + 1];
      for (int n = minInclusive, i = 0; n <= maxInclusive; i++, n++) {
         constants[i] = new ConstantNode(n, integerType());
      }
      return constants;
   }

   /** Creates an array of the specified size and assigns the result of {@link Type#integerType()} to each element. */
   public static Type[] createIntegerTypeArray(int size) {
      Type[] array = new Type[size];
      Type type = integerType();
      Arrays.fill(array, type);
      return array;
   }

   /** Returns a map grouping the specified nodes by their {@code Type}. */
   public static <T extends Node> Map<Type, List<T>> groupByType(T[] nodes) {
      return groupBy(nodes, Node::getType);
   }

   /**
    * Returns a map grouping the specified values according to the specified classification function.
    *
    * @param values
    *           the values to group
    * @param valueToKey
    *           the classification function used to group values
    */
   public static <K, V> Map<K, List<V>> groupBy(V[] values, Function<V, K> valueToKey) {
      Map<K, List<V>> nodesByType = new HashMap<>();
      for (V v : values) {
         addToListOfMap(nodesByType, valueToKey.apply(v), v);
      }
      makeValuesImmutable(nodesByType);
      return nodesByType;
   }

   /**
    * Adds the specified value to the list associated with the specified key in the specified map.
    * <p>
    * If no list is already associated with the specified key then a newly created list is associated with key.
    *
    * @param map
    *           the map that should be updated to include {@code value}
    * @param key
    *           the key whose associated list should be appended with {@code value}
    * @param value
    *           the value that should be added to the list associated with {@code key} in {@code map}
    */
   private static <K, V> void addToListOfMap(Map<K, List<V>> map, K key, V value) {
      List<V> list = map.get(key);
      if (list == null) {
         list = new ArrayList<>();
         map.put(key, list);
      }
      list.add(value);
   }

   /** Replaces each {@code List} stored as a value in the specified {@code Map} with an immutable version. */
   private static <K, V> void makeValuesImmutable(Map<K, List<V>> map) {
      for (Map.Entry<K, List<V>> e : map.entrySet()) {
         map.put(e.getKey(), unmodifiableList(e.getValue()));
      }
   }

   /** Returns randomly selected index of a node from the specified tree. */
   public static int selectSubNodeIndex(Random random, Node tree) {
      int nodeCount = tree.getNodeCount();
      if (nodeCount == 1) {
         // will get here if and only if 'tree' is a terminal (i.e. variable or constant) rather than a function node
         return 0;
      } else {
         return selectSubNodeIndex(random, nodeCount);
      }
   }

   /** Returns a int value between 0 (inclusive) and the specified {@code nodeCount} value minus 1 (exclusive). */
   public static int selectSubNodeIndex(Random random, int nodeCount) {
      // Note: -1 to avoid selecting root node
      return random.nextInt(nodeCount - 1);
   }

   /** Returns a copy of the specified array. */
   public static <T> T[] copyOf(T[] original) {
      return Arrays.copyOf(original, original.length);
   }

   /** Adds each element of the specified array to the specified list. */
   public static <T> void addArray(List<T> list, T[] array) {
      for (T e : array) {
         list.add(e);
      }
   }
}
