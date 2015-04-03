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
   private final Map<Type, List<Function>> operatorsByType;
   private final Map<Signature, List<Function>> operatorsBySignature;

   public FunctionSet(Random random, Function[] operators) {
      this.random = random;
      operatorsByType = new HashMap<>();
      operatorsBySignature = new HashMap<>();
      for (Function operator : operators) {
         addFunctionByType(operator);
         addFunctionBySignature(operator);
      }
   }

   private void addFunctionByType(Function operator) {
      Type type = operator.getSignature().getReturnType();
      List<Function> typeFunctions = operatorsByType.get(type);
      if (typeFunctions == null) {
         typeFunctions = new ArrayList<>();
         operatorsByType.put(type, typeFunctions);
      }
      typeFunctions.add(operator);
   }

   private void addFunctionBySignature(Function operator) {
      Signature signature = operator.getSignature();
      List<Function> typeArgumentCountPairFunctions = operatorsBySignature.get(signature);
      if (typeArgumentCountPairFunctions == null) {
         typeArgumentCountPairFunctions = new ArrayList<>();
         operatorsBySignature.put(signature, typeArgumentCountPairFunctions);
      }
      typeArgumentCountPairFunctions.add(operator);
   }

   /**
    * Returns a randomly selected {@code Function} of the specified {@code Type}.
    *
    * @param type
    *           the required return type of the {@code Function}
    * @return a randomly selected {@code Function} with a return type of {@code type}
    */
   public Function next(Type type) {
      List<Function> typeFunctions = operatorsByType.get(type);
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
      List<Function> operators = operatorsBySignature.get(signature);
      if (operators == null) {
         // TODO remove this check?
         throw new RuntimeException("no match " + current + " " + current.getSignature() + " " + operatorsBySignature);
      }
      int operatorsSize = operators.size();
      if (operatorsSize == 1) {
         // TODO return Optional.empty() instead - so calling called can try something different
         // (e.g. call this method on one of the node's arguments instead)
         return current;
      }
      int randomIndex = random.nextInt(operatorsSize);
      Function next = operators.get(randomIndex);
      if (next == current) {
         int secondRandomIndex = random.nextInt(operatorsSize - 1);
         if (secondRandomIndex >= randomIndex) {
            return operators.get(secondRandomIndex + 1);
         } else {
            return operators.get(secondRandomIndex);
         }
      } else {
         return next;
      }
   }
}
