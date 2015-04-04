package org.oakgp.serialize;

import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oakgp.Type;
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

final class SymbolMap {
   private final Map<Class<? extends Function>, String> classToSymbolMappings;
   private final Map<String, Map<List<Type>, Function>> symbolToInstanceMappings;

   /** @see SymbolMap.Builder#build() */
   private SymbolMap(Map<Class<? extends Function>, String> classToSymbolMappings, Map<String, Map<List<Type>, Function>> symbolToInstanceMappings) {
      this.classToSymbolMappings = classToSymbolMappings;
      this.symbolToInstanceMappings = symbolToInstanceMappings;
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

   public static SymbolMap createDefaultSymbolMap() {
      SymbolMap.Builder builder = new SymbolMap.Builder();

      builder.put("+", new Add());
      builder.put("-", new Subtract());
      builder.put("*", new Multiply());

      builder.put("<", new LessThan());
      builder.put("<=", new LessThanOrEqual());
      builder.put(">", new GreaterThan());
      builder.put(">=", new GreaterThanOrEqual());
      builder.put("=", new Equal());
      builder.put("!=", new NotEqual());

      builder.put("if", new If());

      builder.put("reduce", new Reduce(integerType()));
      builder.put("filter", new Filter(integerType()));
      builder.put("map", new org.oakgp.function.hof.Map(integerType(), booleanType()));

      builder.put("pos?", new IsPositive());
      builder.put("neg?", new IsNegative());
      builder.put("zero?", new IsZero());

      builder.put("count", new Count(integerType()));
      builder.put("count", new Count(booleanType()));

      return builder.build();
   }

   public static class Builder {
      private final Map<Class<? extends Function>, String> classToSymbolMappings = new HashMap<>();
      private final Map<String, Map<List<Type>, Function>> symbolToInstanceMappings = new HashMap<>();

      public Builder put(String symbol, Function function) {
         // TODO add validation around adding things that already exist
         // TODO check subsequent calls after build() do not alter original?
         classToSymbolMappings.put(function.getClass(), symbol);
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

      public SymbolMap build() {
         return new SymbolMap(classToSymbolMappings, symbolToInstanceMappings);
      }
   }
}
