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
   private static final Map<String, Map<List<Type>, Function>> SYMBOL_TO_INSTANCE_MAPPINGS = new HashMap<>();
   private static final Map<Class<? extends Function>, String> CLASS_TO_SYMBOL_MAPPINGS = new HashMap<>();
   static {
      addMapping("+", Add.class);
      addMapping("-", Subtract.class);
      addMapping("*", Multiply.class);

      addMapping("<", LessThan.class);
      addMapping("<=", LessThanOrEqual.class);
      addMapping(">", GreaterThan.class);
      addMapping(">=", GreaterThanOrEqual.class);
      addMapping("=", Equal.class);
      addMapping("!=", NotEqual.class);

      addMapping("if", If.class);

      addMapping("reduce", new Reduce(integerType()));
      addMapping("filter", new Filter(integerType()));
      addMapping("map", new org.oakgp.function.hof.Map(integerType(), booleanType()));

      addMapping("pos?", IsPositive.class);
      addMapping("neg?", IsNegative.class);
      addMapping("zero?", IsZero.class);

      addMapping("count", new Count(integerType()));
      addMapping("count", new Count(booleanType()));
   }

   private static void addMapping(String symbol, Function function) {
      addToInstanceMappings(symbol, function);
      CLASS_TO_SYMBOL_MAPPINGS.put(function.getClass(), symbol);
   }

   private static void addMapping(String symbol, Class<? extends Function> functionClass) {
      addToInstanceMappings(symbol, newInstance(functionClass));
      CLASS_TO_SYMBOL_MAPPINGS.put(functionClass, symbol);
   }

   private static void addToInstanceMappings(String symbol, Function f) {
      Map<List<Type>, Function> m = SYMBOL_TO_INSTANCE_MAPPINGS.get(symbol);
      if (m == null) {
         m = new HashMap<>();
         SYMBOL_TO_INSTANCE_MAPPINGS.put(symbol, m);
      }
      List<Type> key = f.getSignature().getArgumentTypes();
      if (m.containsKey(key)) {
         // TODO is this check required?
         throw new IllegalArgumentException();
      }
      m.put(key, f);
   }

   public String getDisplayName(Function function) {
      Class<? extends Function> functionClass = function.getClass();
      String displayName = CLASS_TO_SYMBOL_MAPPINGS.get(functionClass);
      if (displayName != null) {
         return displayName;
      } else {
         return functionClass.getName();
      }
   }

   public Function getFunction(String symbol, List<Type> types) {
      Map<List<Type>, Function> m = SYMBOL_TO_INSTANCE_MAPPINGS.get(symbol);
      if (m == null) {
         throw new IllegalArgumentException("Could not find function: " + symbol);
      }
      Function f = m.get(types);
      if (f == null) {
         throw new IllegalArgumentException("Could not find version of function: " + symbol + " for: " + types); // TODO + " in: " + m);
      }
      return f;
   }

   private static Function newInstance(Class<? extends Function> functionClass) {
      try {
         return functionClass.newInstance();
      } catch (ReflectiveOperationException e) {
         throw new IllegalArgumentException("Could not create new instance of class: " + functionClass, e);
      }
   }
}
