package org.oakgp.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.ImpureFunction;
import org.oakgp.function.Signature;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.ConstantNode;
import org.oakgp.type.CommonTypes;
import org.oakgp.type.TypeBuilder;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;

// TODO rename, repackage
public class MethodFunction {
   public static Function createFunction(Method method) {
      CommonTypes.stringType(); // TODO
      boolean isStatic = (method.getModifiers() & Modifier.STATIC) != 0;
      if (isStatic) {
         return new StaticMethodFunction(method);
      } else {
         return new InstanceMethodFunction(method);
      }
   }

   public static void main(String[] args) throws Exception {
      Function f1 = createFunction(MethodFunction.class.getMethod("x1", Integer.class, String.class));
      System.out.println(f1.getSignature());
      System.out.println(f1.evaluate(//
                  ChildNodes.createChildNodes( //
                              new ConstantNode(1, CommonTypes.integerType()), //
                              new ConstantNode("world", CommonTypes.stringType())), //
                  null));

      TypeBuilder.name(MethodFunction.class).build();
      Function f2 = createFunction(MethodFunction.class.getMethod("x2", int.class, String.class));
      System.out.println(f2.getSignature());
      System.out.println(f2.evaluate(//
                  ChildNodes.createChildNodes( //
                              new ConstantNode(new MethodFunction(), CommonTypes.integerType()), //
                              new ConstantNode(1, CommonTypes.integerType()), //
                              new ConstantNode("world", CommonTypes.stringType())), //
                  null));
   }

   public static String x1(Integer i, String s) {
      return "hi " + i + " " + s;
   }

   public boolean x2(int i, String s) {
      return false;
   }

   private static class InstanceMethodFunction implements ImpureFunction {
      private final Method method;
      private final Signature signature;

      InstanceMethodFunction(Method method) {
         this.method = method;

         Type returnType = Types.find(method.getReturnType());

         Parameter[] methodParameters = method.getParameters();
         Type[] argumentTypes = new Type[methodParameters.length + 1];
         argumentTypes[0] = Types.find(method.getDeclaringClass());
         for (int i = 0; i < methodParameters.length; i++) {
            argumentTypes[i + 1] = Types.find(methodParameters[i].getType());
         }

         this.signature = Signature.createSignature(returnType, argumentTypes);
      }

      @Override
      public Signature getSignature() {
         return signature;
      }

      @Override
      public Object evaluate(ChildNodes arguments, Assignments assignments) {
         Object target = arguments.first().evaluate(assignments);
         Object[] methodArguments = new Object[arguments.size() - 1];
         for (int i = 0; i < methodArguments.length; i++) {
            methodArguments[i] = arguments.getNode(i + 1).evaluate(assignments);
         }

         try {
            return method.invoke(target, methodArguments);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      @Override
      public String getDisplayName() {
         return method.getName();
      }
   }

   private static class StaticMethodFunction implements ImpureFunction {
      private final Method method;
      private final Signature signature;

      StaticMethodFunction(Method method) {
         this.method = method;

         Type returnType = Types.find(method.getReturnType());

         Parameter[] methodParameters = method.getParameters();
         Type[] argumentTypes = new Type[methodParameters.length];
         for (int i = 0; i < methodParameters.length; i++) {
            argumentTypes[i] = Types.find(methodParameters[i].getType());
         }

         this.signature = Signature.createSignature(returnType, argumentTypes);
      }

      @Override
      public Signature getSignature() {
         return signature;
      }

      @Override
      public Object evaluate(ChildNodes arguments, Assignments assignments) {
         Object[] methodArguments = new Object[arguments.size()];
         for (int i = 0; i < methodArguments.length; i++) {
            methodArguments[i] = arguments.getNode(i).evaluate(assignments);
         }

         try {
            return method.invoke(null, methodArguments);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      @Override
      public String getDisplayName() {
         return method.getName();
      }
   }
}
