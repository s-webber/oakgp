package org.oakgp;

public final class Signature { // TODO move to org.oakgp.operator?
   private final Type returnType;
   private final Type[] argumentTypes;

   public static Signature createSignature(Type returnType, Type... argumentTypes) {
      Type[] copy = new Type[argumentTypes.length];
      System.arraycopy(argumentTypes, 0, copy, 0, argumentTypes.length);
      return new Signature(returnType, copy);
   }

   private Signature(Type returnType, Type[] argumentTypes) {
      this.returnType = returnType;
      this.argumentTypes = argumentTypes;
   }

   public Type getReturnType() {
      return returnType;
   }

   public Type getArgumentType(int index) {
      return argumentTypes[index];
   }

   public int getArgumentTypesLength() { // TODO rename?
      return argumentTypes.length;
   }
}
