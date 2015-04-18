package org.oakgp;

import static org.oakgp.util.Utils.groupBy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oakgp.function.Function;

/** Represents the set of possible {@code Function} implementations to use during a genetic programming run. */
public final class FunctionSet {
   private final Map<String, Map<List<Type>, Function>> symbolToInstanceMappings;
   private final Map<Type, List<Function>> functionsByType;
   private final Map<Signature, List<Function>> functionsBySignature;

   public FunctionSet(Function... functions) {
      this.symbolToInstanceMappings = createInstanceMappings(functions);
      this.functionsByType = groupBy(functions, f -> f.getSignature().getReturnType());
      this.functionsBySignature = groupBy(functions, f -> f.getSignature());
   }

   private static Map<String, Map<List<Type>, Function>> createInstanceMappings(Function[] functions) {
      Map<String, Map<List<Type>, Function>> m = new HashMap<>();
      for (Function f : functions) {
         addToInstanceMappings(m, f);
      }
      return m;
   }

   private static void addToInstanceMappings(Map<String, Map<List<Type>, Function>> symbolToInstanceMappings, Function f) {
      String displayName = f.getDisplayName();
      Map<List<Type>, Function> m = symbolToInstanceMappings.get(displayName);
      if (m == null) {
         m = new HashMap<>();
         symbolToInstanceMappings.put(displayName, m);
      }
      List<Type> key = f.getSignature().getArgumentTypes();
      if (m.containsKey(key)) {
         throw new IllegalArgumentException("Functions " + m.get(key) + " and " + f + " both have the display name " + key + " and signature "
               + f.getSignature());
      }
      m.put(key, f);
   }

   public Function getFunction(String symbol) { // TODO test
      Map<List<Type>, Function> m = symbolToInstanceMappings.get(symbol);
      if (m == null) {
         throw new IllegalArgumentException("Could not find function: " + symbol);
      }
      if (m.size() > 1) {
         throw new IllegalArgumentException("Found more than one function: " + symbol + " " + m);
      }
      return m.values().iterator().next();
   }

   public Function getFunction(String symbol, List<Type> types) {
      Map<List<Type>, Function> m = symbolToInstanceMappings.get(symbol);
      if (m == null) {
         throw new IllegalArgumentException("Could not find function: " + symbol);
      }
      Function f = m.get(types);
      if (f == null) {
         throw new IllegalArgumentException("Could not find version of function: " + symbol + " for: " + types + " in: " + m);
      }
      return f;
   }

   public List<Function> getByType(Type type) {
      return functionsByType.get(type);
   }

   public List<Function> getBySignature(Signature signature) {
      return functionsBySignature.get(signature);
   }
}
