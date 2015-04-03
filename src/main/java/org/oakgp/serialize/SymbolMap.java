package org.oakgp.serialize;

import java.util.HashMap;
import java.util.Map;

import org.oakgp.function.Function;
import org.oakgp.function.choice.If;
import org.oakgp.function.classify.IsNegative;
import org.oakgp.function.classify.IsPositive;
import org.oakgp.function.classify.IsZero;
import org.oakgp.function.compare.Equal;
import org.oakgp.function.compare.GreaterThan;
import org.oakgp.function.compare.GreaterThanOrEqual;
import org.oakgp.function.compare.LessThan;
import org.oakgp.function.compare.LessThanOrEqual;
import org.oakgp.function.compare.NotEqual;
import org.oakgp.function.hof.Reduce;
import org.oakgp.function.math.Add;
import org.oakgp.function.math.Multiply;
import org.oakgp.function.math.Subtract;

final class SymbolMap {
   private static final Map<String, Class<? extends Function>> SYMBOL_TO_CLASS_MAPPINGS = new HashMap<>();
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

      addMapping("reduce", Reduce.class);

      addMapping("pos?", IsPositive.class);
      addMapping("neg?", IsNegative.class);
      addMapping("zero?", IsZero.class);
   }

   private static void addMapping(String symbol, Class<? extends Function> functionClass) {
      SYMBOL_TO_CLASS_MAPPINGS.put(symbol, functionClass);
      CLASS_TO_SYMBOL_MAPPINGS.put(functionClass, symbol);
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

   public Function getFunction(String symbol) {
      Class<? extends Function> functionClass = SYMBOL_TO_CLASS_MAPPINGS.get(symbol);
      if (functionClass == null) {
         functionClass = findClass(symbol);
      }
      return newInstance(functionClass);
   }

   @SuppressWarnings("unchecked")
   private Class<? extends Function> findClass(String className) {
      try {
         return (Class<? extends Function>) Class.forName(className);
      } catch (ClassNotFoundException e) {
         throw new IllegalArgumentException("Could not find class: " + className, e);
      }
   }

   private Function newInstance(Class<? extends Function> functionClass) {
      try {
         return functionClass.newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
         throw new IllegalArgumentException("Could not create new instance of class: " + functionClass, e);
      }
   }
}
