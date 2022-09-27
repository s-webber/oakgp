package org.oakgp.type;

final class RawTypeKey {
   private final String name;
   private final int arity;

   RawTypeKey(String name, int arity) {
      this.name = name;
      this.arity = arity;
   }

   @Override
   public int hashCode() {
      return name.hashCode() * (arity + 1);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }

      RawTypeKey other = (RawTypeKey) obj;
      return arity == other.arity && name.equals(other.name);
   }
}
