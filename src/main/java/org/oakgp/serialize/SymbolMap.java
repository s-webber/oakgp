package org.oakgp.serialize;

import java.util.HashMap;
import java.util.Map;

import org.oakgp.function.Function;
import org.oakgp.function.choice.If;
import org.oakgp.function.compare.Equal;
import org.oakgp.function.compare.GreaterThan;
import org.oakgp.function.compare.GreaterThanOrEqual;
import org.oakgp.function.compare.LessThan;
import org.oakgp.function.compare.LessThanOrEqual;
import org.oakgp.function.compare.NotEqual;
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
   }

   private static void addMapping(String symbol, Class<? extends Function> operatorClass) {
      SYMBOL_TO_CLASS_MAPPINGS.put(symbol, operatorClass);
      CLASS_TO_SYMBOL_MAPPINGS.put(operatorClass, symbol);
   }

   public String getDisplayName(Function operator) {
      Class<? extends Function> operatorClass = operator.getClass();
      String displayName = CLASS_TO_SYMBOL_MAPPINGS.get(operatorClass);
      if (displayName != null) {
         return displayName;
      } else {
         return operatorClass.getName();
      }
   }

   public Function getFunction(String symbol) {
      Class<? extends Function> operatorClass = SYMBOL_TO_CLASS_MAPPINGS.get(symbol);
      if (operatorClass == null) {
         operatorClass = findClass(symbol);
      }
      return newInstance(operatorClass);
   }

   @SuppressWarnings("unchecked")
   private Class<? extends Function> findClass(String className) {
      try {
         return (Class<? extends Function>) Class.forName(className);
      } catch (ClassNotFoundException e) {
         throw new IllegalArgumentException("Could not find class: " + className, e);
      }
   }

   private Function newInstance(Class<? extends Function> operatorClass) {
      try {
         return operatorClass.newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
         throw new IllegalArgumentException("Could not create new instance of class: " + operatorClass, e);
      }
   }
}
