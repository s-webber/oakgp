package org.oakgp.node;

import java.util.Arrays;

import org.oakgp.Assignments;
import org.oakgp.type.Types.Type;

public final class ProgramNode implements Node, AutomaticallyDefinedFunctions {
   private final Node[] automaticallyDefinedFunctions;

   public ProgramNode(Node[] automaticallyDefinedFunctions) {
      this.automaticallyDefinedFunctions = automaticallyDefinedFunctions;
   }

   @Override
   public boolean isSimplified() {
      for (Node n : automaticallyDefinedFunctions) {
         if (!n.isSimplified()) {
            return false;
         }
      }
      return true;
   }

   @Override
   public <T> T evaluate(Assignments assignments, AutomaticallyDefinedFunctions adfs) {
      return automaticallyDefinedFunctions[0].evaluate(assignments, this);
   }

   @Override
   public int getNodeCount() {
      int count = 0;
      for (Node n : automaticallyDefinedFunctions) {
         count += n.getNodeCount();
      }
      return count;
   }

   @Override
   public int getHeight() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Type getType() {
      // return automaticallyDefinedFunctions[0].getType();
      throw new UnsupportedOperationException();
   }

   @Override
   public NodeType getNodeType() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Node getAbstractDefinedFunction(int i) {
      return automaticallyDefinedFunctions[i];
   }

   public int adfCount() { // TODO rename
      return automaticallyDefinedFunctions.length;
   }

   @Override
   public String toString() {
      return Arrays.toString(automaticallyDefinedFunctions);
   }
}
