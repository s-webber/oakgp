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
   private final Map<TypeArgumentCountPair, List<Operator>> operatorsByTypeArgumentCountPair;

   // TODO refactor
   public FunctionSet(Random random, Operator[] operators) {
      this.random = random;
      operatorsByType = new HashMap<>();
      operatorsByTypeArgumentCountPair = new HashMap<>();
      for (Operator operator : operators) {
         Signature signature = operator.getSignature();
         Type type = signature.getReturnType();
         List<Operator> typeOperators = operatorsByType.get(type);
         if (typeOperators == null) {
            typeOperators = new ArrayList<>();
            operatorsByType.put(type, typeOperators);
         }
         TypeArgumentCountPair p = new TypeArgumentCountPair(type, signature.getArgumentTypesLength());
         List<Operator> typeArgumentCountPairOperators = operatorsByTypeArgumentCountPair.get(p);
         if (typeArgumentCountPairOperators == null) {
            typeArgumentCountPairOperators = new ArrayList<>();
            operatorsByTypeArgumentCountPair.put(p, typeArgumentCountPairOperators);
         }
         typeOperators.add(operator);
         typeArgumentCountPairOperators.add(operator);
      }
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
      TypeArgumentCountPair p = new TypeArgumentCountPair(signature.getReturnType(), signature.getArgumentTypesLength());
      List<Operator> operators = operatorsByTypeArgumentCountPair.get(p);
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

   private static class TypeArgumentCountPair {
      final Type type;
      final int argumentCount;

      public TypeArgumentCountPair(Type type, int argumentCount) {
         this.type = type;
         this.argumentCount = argumentCount;
      }

      @Override
      public int hashCode() {
         return type.hashCode() + argumentCount;
      }

      @Override
      public boolean equals(Object o) {
         TypeArgumentCountPair p = (TypeArgumentCountPair) o;
         return type == p.type && argumentCount == p.argumentCount;
      }
   }
}
