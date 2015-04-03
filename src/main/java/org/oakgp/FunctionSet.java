package org.oakgp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oakgp.operator.Operator;
import org.oakgp.util.Random;

/** Represents the set of possible {@code Operator} implementations to use during a genetic programming run. */
public final class FunctionSet {
   private final Random random;
   private final Map<Type, List<Operator>> operatorsByType;
   private final Map<Signature, List<Operator>> operatorsBySignature;

   public FunctionSet(Random random, Operator[] operators) {
      this.random = random;
      operatorsByType = new HashMap<>();
      operatorsBySignature = new HashMap<>();
      for (Operator operator : operators) {
         addOperatorByType(operator);
         addOperatorBySignature(operator);
      }
   }

   private void addOperatorByType(Operator operator) {
      Type type = operator.getSignature().getReturnType();
      List<Operator> typeOperators = operatorsByType.get(type);
      if (typeOperators == null) {
         typeOperators = new ArrayList<>();
         operatorsByType.put(type, typeOperators);
      }
      typeOperators.add(operator);
   }

   private void addOperatorBySignature(Operator operator) {
      Signature signature = operator.getSignature();
      List<Operator> typeArgumentCountPairOperators = operatorsBySignature.get(signature);
      if (typeArgumentCountPairOperators == null) {
         typeArgumentCountPairOperators = new ArrayList<>();
         operatorsBySignature.put(signature, typeArgumentCountPairOperators);
      }
      typeArgumentCountPairOperators.add(operator);
   }

   /**
    * Returns a randomly selected {@code Operator} of the specified {@code Type}.
    *
    * @param type
    *           the required return type of the {@code Operator}
    * @return a randomly selected {@code Operator} with a return type of {@code type}
    */
   public Operator next(Type type) {
      List<Operator> typeOperators = operatorsByType.get(type);
      if (typeOperators == null) { // TODO remove this check?
         throw new RuntimeException("No " + type);
      }
      int index = random.nextInt(typeOperators.size());
      return typeOperators.get(index);
   }

   /**
    * Returns a randomly selected {@code Operator} that is not the same as the specified {@code Operator}.
    *
    * @param current
    *           the current {@code Operator} that the returned result should be an alternative to (i.e. not the same as)
    * @return a randomly selected {@code Operator} that is not the same as the specified {@code Operator}
    */
   public Operator nextAlternative(Operator current) {
      Signature signature = current.getSignature();
      List<Operator> operators = operatorsBySignature.get(signature);
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
      Operator next = operators.get(randomIndex);
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
