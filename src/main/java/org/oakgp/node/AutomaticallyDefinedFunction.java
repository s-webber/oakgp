package org.oakgp.node;

import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;

public class AutomaticallyDefinedFunction implements Function { // TODO move
   private final int id;
   private final Signature signature;

   public AutomaticallyDefinedFunction(int id, Signature signature) {
      this.id = id;
      this.signature = signature;
   }

   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments, AutomaticallyDefinedFunctions adfs) {
      Object[] values = new Object[assignments.size() + arguments.size()];
      for (int i = 0; i < assignments.size(); i++) {
         values[i] = assignments.get(i);
      }
      for (int i = 0; i < arguments.size(); i++) {
         values[i + assignments.size()] = arguments.getNode(i).evaluate(assignments, adfs);
      }
      return adfs.getAbstractDefinedFunction(id).evaluate(Assignments.createAssignments(values), adfs);
   }

   @Override
   public Signature getSignature() {
      return signature;
   }

   @Override
   public String toString() {
      return "adf" + id;
   }

   @Override
   public boolean isPure() {
      return false;
   }
}
