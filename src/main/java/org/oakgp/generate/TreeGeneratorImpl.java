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
package org.oakgp.generate;

import java.util.Objects;
import java.util.function.IntPredicate;

import org.oakgp.function.Signature;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.primitive.PrimitiveSet;
import org.oakgp.type.Types.Type;
import org.oakgp.util.Random;

/**
 * Provides different strategies for creating a tree data structure.
 * <p>
 * Can be used to create randomly generate the initial population of a genetic programming run.
 *
 * @see #full(PrimitiveSet)
 * @see #grow(PrimitiveSet, Random)
 */
public final class TreeGeneratorImpl implements TreeGenerator {
   private final PrimitiveSet primitiveSet;
   private final IntPredicate strategy;

   /**
    * Creates a {@code TreeGenerator} that uses the "full" approach to creating trees.
    * <p>
    * The "full" approach constructs trees where all terminal nodes (i.e. leaf nodes) are at the same depth.
    *
    * @param primitiveSet
    *           the collection of functions, variables and constants from which tree will be constructed
    * @return a {@code TreeGenerator} that uses the "full" approach to creating trees.
    */
   public static TreeGenerator full(PrimitiveSet primitiveSet) {
      return new TreeGeneratorImpl(primitiveSet, d -> d > 0);
   }

   /**
    * Creates a {@code TreeGenerator} that uses the "grow" approach to creating trees.
    * <p>
    * The "grow" approach constructs trees where terminal nodes (i.e. leaf nodes) are located at random depths, within a maximum limit.
    *
    * @param primitiveSet
    *           the collection of functions, variables and constants from which tree will be constructed
    * @param random
    *           used to randomly determine the structure of the generated trees
    * @return a {@code TreeGenerator} that uses the "grow" approach to creating trees.
    */
   public static TreeGenerator grow(PrimitiveSet primitiveSet, Random random) {
      return new TreeGeneratorImpl(primitiveSet, d -> d > 0 && random.nextBoolean());
   }

   /**
    * @see #full(PrimitiveSet)
    * @see #grow(PrimitiveSet, Random)
    */
   private TreeGeneratorImpl(PrimitiveSet primitiveSet, IntPredicate strategy) {
      Objects.requireNonNull(primitiveSet);
      this.primitiveSet = primitiveSet;
      this.strategy = strategy;
   }

   @Override
   public Node generate(Type type, int depth) {
      if (shouldCreateFunction(type, depth)) {
         FunctionSet.Key key = primitiveSet.nextFunction(type);
         Signature signature = key.getSignature();
         Node[] args = new Node[signature.getArgumentTypesLength()];
         for (int i = 0; i < args.length; i++) {
            Type argType = signature.getArgumentType(i);
            Node arg = generate(argType, depth - 1);
            args[i] = arg;
         }
         if (key.getReturnType() != type) {
            // should never get here
            throw new RuntimeException();
         }
         return new FunctionNode(key.getFunction(), type, args);
      } else {
         return primitiveSet.nextTerminal(type);
      }
   }

   private boolean shouldCreateFunction(Type type, int depth) {
      if (!primitiveSet.hasTerminals(type)) {
         // Logger.getGlobal().info("no terminal for " + type + " depth " + depth + " " + strategy.test(depth)); // TODO
         return true;
      } else if (!primitiveSet.hasFunctions(type)) {
         // Logger.getGlobal().info("no function for " + type + " depth " + depth + " " + strategy.test(depth)); // TODO
         return false;
      } else {
         return strategy.test(depth);
      }
   }
}
