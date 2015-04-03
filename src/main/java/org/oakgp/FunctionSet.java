package org.oakgp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oakgp.function.Function;
import org.oakgp.util.Random;

/** Represents the set of possible {@code Function} implementations to use during a genetic programming run. */
public final class FunctionSet {
   private final Random random;
   private final Map<Type, List<Function>> functionsByType;
   private final Map<Signature, List<Function>> functionsBySignature;

   public FunctionSet(Random random, Function[] functions) {
      this.random = random;
      functionsByType = new HashMap<>();
      functionsBySignature = new HashMap<>();
      for (Function function : functions) {
         addFunctionByType(function);
         addFunctionBySignature(function);
      }
   }

   private void addFunctionByType(Function function) {
      Type type = function.getSignature().getReturnType();
      List<Function> typeFunctions = functionsByType.get(type);
      if (typeFunctions == null) {
         typeFunctions = new ArrayList<>();
         functionsByType.put(type, typeFunctions);
      }
      typeFunctions.add(function);
   }

   private void addFunctionBySignature(Function function) {
      Signature signature = function.getSignature();
      List<Function> typeArgumentCountPairFunctions = functionsBySignature.get(signature);
      if (typeArgumentCountPairFunctions == null) {
         typeArgumentCountPairFunctions = new ArrayList<>();
         functionsBySignature.put(signature, typeArgumentCountPairFunctions);
      }
      typeArgumentCountPairFunctions.add(function);
   }

   /**
    * Returns a randomly selected {@code Function} of the specified {@code Type}.
    *
    * @param type
    *           the required return type of the {@code Function}
    * @return a randomly selected {@code Function} with a return type of {@code type}
    */
   public Function next(Type type) {
      List<Function> typeFunctions = functionsByType.get(type);
      if (typeFunctions == null) { // TODO remove this check?
         throw new RuntimeException("No " + type);
      }
      int index = random.nextInt(typeFunctions.size());
      return typeFunctions.get(index);
   }

   /**
    * Returns a randomly selected {@code Function} that is not the same as the specified {@code Function}.
    *
    * @param current
    *           the current {@code Function} that the returned result should be an alternative to (i.e. not the same as)
    * @return a randomly selected {@code Function} that is not the same as the specified {@code Function}
    */
   public Function nextAlternative(Function current) {
      Signature signature = current.getSignature();
      List<Function> functions = functionsBySignature.get(signature);
      if (functions == null) {
         // TODO remove this check?
         throw new RuntimeException("no match " + current + " " + current.getSignature() + " " + functionsBySignature);
      }
      int functionsSize = functions.size();
      if (functionsSize == 1) {
         // TODO return Optional.empty() instead - so calling called can try something different
         // (e.g. call this method on one of the node's arguments instead)
         return current;
      }
      int randomIndex = random.nextInt(functionsSize);
      Function next = functions.get(randomIndex);
      if (next == current) {
         int secondRandomIndex = random.nextInt(functionsSize - 1);
         if (secondRandomIndex >= randomIndex) {
            return functions.get(secondRandomIndex + 1);
         } else {
            return functions.get(secondRandomIndex);
         }
      } else {
         return next;
      }
   }
}
