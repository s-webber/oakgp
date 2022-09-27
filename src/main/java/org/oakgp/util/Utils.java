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
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.groupingBy;
import static org.oakgp.node.NodeType.isFunction;
import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.integerType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Stream;

import org.oakgp.function.coll.Set;
import org.oakgp.function.coll.Sort;
import org.oakgp.function.coll.SortedSet;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.type.CommonTypes;
import org.oakgp.type.Types.Type;

/** Utility methods that support the functionality provided by the rest of the framework. */
public final class Utils {
   /** Represents the boolean value {@code true}. */
   public static final ConstantNode TRUE_NODE = new ConstantNode(TRUE, booleanType());
   /** Represents the boolean value {@code false}. */
   public static final ConstantNode FALSE_NODE = new ConstantNode(FALSE, booleanType());
   // TODO have immutable list of TRUE_NODE and FALSE_NODE so can do constants.addAll(BOOLEANS) or Utils.createBooleanConstants()
   // TODO check using TRUE_NODE and FALSE_NODE in examples rather than create new ConstantNodes each time

   /** Private constructor as all methods are static. */
   private Utils() {
      // do nothing
   }

   /**
    * Creates an array of the specified size and assigns the result of {@link CommonTypes#integerType()} to each element.
    */
   public static Type[] createIntegerTypeArray(int size) { // TODO move to TestUtils
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
      return groupBy(Arrays.stream(values), valueToKey);
   }

   /**
    * Returns a map grouping the specified values according to the specified classification function.
    *
    * @param values
    *           the values to group
    * @param valueToKey
    *           the classification function used to group values
    */
   public static <K, V> Map<K, List<V>> groupBy(Collection<V> values, Function<V, K> valueToKey) {
      return groupBy(values.stream(), valueToKey);
   }

   private static <K, V> Map<K, List<V>> groupBy(Stream<V> values, Function<V, K> valueToKey) {
      Map<K, List<V>> nodesByType = values.collect(groupingBy(valueToKey));
      return unmodifiableMapOfLists(nodesByType);
   }

   /** Replaces each {@code List} stored as a value in the specified {@code Map} with an immutable version. */
   private static <K, V> Map<K, List<V>> unmodifiableMapOfLists(Map<K, List<V>> input) {
      Map<K, List<V>> output = new HashMap<>();
      for (Map.Entry<K, List<V>> e : input.entrySet()) {
         output.put(e.getKey(), unmodifiableList(e.getValue()));
      }
      return unmodifiableMap(output);
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

   // TODO this is tested via MinTest and MaxTest - add unit-tests to UtilsTest
   public static Node toOrderedNode(FunctionNode input) {
      Node result = toNode(input.getFunction(), input.getType(), toOrderedList(input));
      return result.equals(input) ? null : result;
   }

   // TODO this is tested via toOrderedNode and Min - add unit-tests to UtilsTest
   public static Node toNode(org.oakgp.function.Function function, Type returnType, Collection<Node> children) {
      Iterator<Node> itr = children.iterator();
      Node node = itr.next();
      while (itr.hasNext()) {
         node = new FunctionNode(function, returnType, ChildNodes.createChildNodes(itr.next(), node));
      }
      return node;
   }

   // TODO this is tested via toOrderedNode and Min - add unit-tests to UtilsTest
   public static Collection<Node> toOrderedList(FunctionNode input) {
      Collection<Node> result = new TreeSet<>(NodeComparator.NODE_COMPARATOR);

      org.oakgp.function.Function function = input.getFunction();
      List<Node> queue = new ArrayList<>();
      addChildrenToList(queue, input);
      do {
         Node next = queue.remove(queue.size() - 1);
         if (isFunction(next) && ((FunctionNode) next).getFunction() == function) {
            addChildrenToList(queue, (FunctionNode) next);
         } else {
            result.add(next);
         }
      } while (!queue.isEmpty());

      return result;
   }

   private static void addChildrenToList(List<Node> queue, FunctionNode input) {
      for (int i = 0; i < input.getChildren().size(); i++) {
         queue.add(input.getChildren().getNode(i));
      }
   }

   // TODO this is tested via Sort, SortedSet and Sum - add unit-tests to UtilsTest
   public static Node recursivelyRemoveSort(Node n) {
      if (isFunction(n)) {
         FunctionNode fn = (FunctionNode) n;
         org.oakgp.function.Function f = fn.getFunction();
         if (f.getClass() == org.oakgp.function.hof.Map.class) { // TODO
            // (map f (sort v0)) -> (map f v0)
            // (map f (sorted-set v0)) -> (map f (set v0))
            Node input = fn.getChildren().second();
            Node newInput = recursivelyRemoveSort(input);
            if (newInput != input) {
               return new FunctionNode(fn, fn.getChildren().replaceAt(1, newInput));
            }
         } else if (f == SortedSet.getSingleton()) {
            // (sorted-set v0) -> (set v0)
            return new FunctionNode(Set.getSingleton(), fn.getType(), fn.getChildren());
         } else if (f.getClass() == Sort.class) {
            // (sort v0) -> v0
            return fn.getChildren().first();
         } else if (f == Set.getSingleton()) {
            // (set (map f (sort v0))) -> (set (map f v0))
            Node input = fn.getChildren().first();
            Node newInput = recursivelyRemoveSort(input);
            if (newInput != input) {
               return new FunctionNode(fn, ChildNodes.createChildNodes(newInput));
            }
         }
      }

      return n;
   }
}
