package org.oakgp;

import java.util.Objects;
import java.util.function.IntPredicate;

import org.oakgp.function.Function;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.util.Random;

public final class TreeGeneratorImpl implements TreeGenerator {
   private final PrimitiveSet primitiveSet;
   private final IntPredicate strategy;

   public static TreeGeneratorImpl full(PrimitiveSet primitiveSet) {
      return new TreeGeneratorImpl(primitiveSet, d -> d > 0);
   }

   public static TreeGeneratorImpl grow(PrimitiveSet primitiveSet, Random random) {
      return new TreeGeneratorImpl(primitiveSet, d -> d > 0 && random.nextBoolean());
   }

   private TreeGeneratorImpl(PrimitiveSet primitiveSet, IntPredicate strategy) {
      Objects.requireNonNull(primitiveSet);
      this.primitiveSet = primitiveSet;
      this.strategy = strategy;
   }

   @Override
   public Node generate(Type type, int depth) {
      if (strategy.test(depth)) {
         Function function = primitiveSet.nextFunction(type);
         Signature signature = function.getSignature();
         Node[] args = new Node[signature.getArgumentTypesLength()];
         for (int i = 0; i < args.length; i++) {
            Type argType = signature.getArgumentType(i);
            Node arg = generate(argType, depth - 1);
            args[i] = arg;
         }
         return new FunctionNode(function, args);
      } else {
         return primitiveSet.nextTerminal(type);
      }
   }
}
