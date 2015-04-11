package org.oakgp;

import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerType;
import static org.oakgp.util.Utils.groupBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oakgp.function.Function;
import org.oakgp.function.choice.If;
import org.oakgp.function.classify.IsNegative;
import org.oakgp.function.classify.IsPositive;
import org.oakgp.function.classify.IsZero;
import org.oakgp.function.coll.Count;
import org.oakgp.function.compare.Equal;
import org.oakgp.function.compare.GreaterThan;
import org.oakgp.function.compare.GreaterThanOrEqual;
import org.oakgp.function.compare.LessThan;
import org.oakgp.function.compare.LessThanOrEqual;
import org.oakgp.function.compare.NotEqual;
import org.oakgp.function.hof.Filter;
import org.oakgp.function.hof.Reduce;
import org.oakgp.function.math.Add;
import org.oakgp.function.math.Multiply;
import org.oakgp.function.math.Subtract;

/** Represents the set of possible {@code Function} implementations to use during a genetic programming run. */
public final class FunctionSet {
   // TODO tidy up how member variables are created
   // TODO remove Builder - replace with new FunctionSet(Function...)

   private final Map<Class<? extends Function>, String> classToSymbolMappings;
   private final Map<String, Map<List<Type>, Function>> symbolToInstanceMappings;
   private final Map<Type, List<Function>> functionsByType;
   private final Map<Signature, List<Function>> functionsBySignature;

   /** @see FunctionSet.Builder#build() */
   private FunctionSet(Map<Class<? extends Function>, String> classToSymbolMappings, Map<String, Map<List<Type>, Function>> symbolToInstanceMappings,
         List<Function> functions) {
      this.classToSymbolMappings = classToSymbolMappings;
      this.symbolToInstanceMappings = symbolToInstanceMappings;
      Function[] functionsArray = functions.toArray(new Function[functions.size()]);
      functionsByType = groupBy(functionsArray, f -> f.getSignature().getReturnType());
      functionsBySignature = groupBy(functionsArray, f -> f.getSignature());
   }

   public String getDisplayName(Function function) {
      Class<? extends Function> functionClass = function.getClass();
      String displayName = classToSymbolMappings.get(functionClass);
      if (displayName != null) {
         return displayName;
      } else {
         return functionClass.getName();
      }
   }

   public Function getFunction(String symbol, List<Type> types) {
      Map<List<Type>, Function> m = symbolToInstanceMappings.get(symbol);
      if (m == null) {
         throw new IllegalArgumentException("Could not find function: " + symbol);
      }
      Function f = m.get(types);
      if (f == null) {
         throw new IllegalArgumentException("Could not find version of function: " + symbol + " for: " + types); // TODO + " in: " + m);
      }
      return f;
   }

   public List<Function> getByType(Type type) {
      return functionsByType.get(type);
   }

   public List<Function> getBySignature(Signature signature) {
      return functionsBySignature.get(signature);
   }

   public static FunctionSet createDefaultFunctionSet() {
      FunctionSet.Builder builder = new FunctionSet.Builder();

      builder.put(new Add());
      builder.put(new Subtract());
      builder.put(new Multiply());

      builder.put(new LessThan());
      builder.put(new LessThanOrEqual());
      builder.put(new GreaterThan());
      builder.put(new GreaterThanOrEqual());
      builder.put(new Equal());
      builder.put(new NotEqual());

      builder.put(new If());

      builder.put(new Reduce(integerType()));
      builder.put(new Filter(integerType()));
      builder.put(new org.oakgp.function.hof.Map(integerType(), booleanType()));

      builder.put(new IsPositive());
      builder.put(new IsNegative());
      builder.put(new IsZero());

      builder.put(new Count(integerType()));
      builder.put(new Count(booleanType()));

      return builder.build();
   }

   /** Implementation of the builder pattern to aid the construction of a {@code FunctionSet}. */
   public static final class Builder {
      private final Map<Class<? extends Function>, String> classToSymbolMappings = new HashMap<>();
      private final Map<String, Map<List<Type>, Function>> symbolToInstanceMappings = new HashMap<>();
      private final List<Function> functions = new ArrayList<>();

      public Builder put(Function function) {
         String symbol = function.getDisplayName();
         // TODO add validation around adding things that already exist
         // TODO check subsequent calls after build() do not alter original?
         classToSymbolMappings.put(function.getClass(), symbol);
         functions.add(function);
         addToInstanceMappings(symbol, function);
         return this;
      }

      private void addToInstanceMappings(String symbol, Function f) {
         Map<List<Type>, Function> m = symbolToInstanceMappings.get(symbol);
         if (m == null) {
            m = new HashMap<>();
            symbolToInstanceMappings.put(symbol, m);
         }
         List<Type> key = f.getSignature().getArgumentTypes();
         if (m.containsKey(key)) {
            // TODO is this check required?
            throw new IllegalArgumentException();
         }
         m.put(key, f);
      }

      public FunctionSet build() {
         return new FunctionSet(classToSymbolMappings, symbolToInstanceMappings, functions);
      }
   }
}
