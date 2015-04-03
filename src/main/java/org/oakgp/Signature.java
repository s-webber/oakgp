package org.oakgp;

import java.util.Arrays;

public final class Signature { // TODO move to org.oakgp.operator?
   private final Type returnType;
   private final Type[] argumentTypes;
   private int hashCode;

   public static Signature createSignature(Type returnType, Type... argumentTypes) {
      Type[] copy = new Type[argumentTypes.length];
      System.arraycopy(argumentTypes, 0, copy, 0, argumentTypes.length);
      return new Signature(returnType, copy);
   }

   private Signature(Type returnType, Type[] argumentTypes) {
      this.returnType = returnType;
      this.argumentTypes = argumentTypes;
      this.hashCode = (returnType.hashCode() * 31) * Arrays.hashCode(argumentTypes);
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

   @Override
   public int hashCode() {
      return hashCode;
   }

   @Override
   public boolean equals(Object o) {
      if (o instanceof Signature) {
         Signature s = (Signature) o;
         return this.returnType == s.returnType && Arrays.equals(this.argumentTypes, s.argumentTypes);
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return returnType + " " + Arrays.toString(argumentTypes);
   }
}
