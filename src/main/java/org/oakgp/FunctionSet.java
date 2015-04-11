package org.oakgp;

import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerType;
import static org.oakgp.util.Utils.groupBy;

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
      return new FunctionSet(

      new Add(), new Subtract(), new Multiply(),

            new LessThan(), new LessThanOrEqual(), new GreaterThan(), new GreaterThanOrEqual(), new Equal(), new NotEqual(),

            new If(),

            new Reduce(integerType()), new Filter(integerType()), new org.oakgp.function.hof.Map(integerType(), booleanType()),

            new IsPositive(), new IsNegative(), new IsZero(),

            new Count(integerType()), new Count(booleanType()));
   }
}
