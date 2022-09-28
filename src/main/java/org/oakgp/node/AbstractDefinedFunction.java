package org.oakgp.node;

import org.oakgp.Assignments;
import org.oakgp.type.Types.Type;

public class AbstractDefinedFunction extends TerminalNode {
   private final int adfId;
   private final Type type;

   public AbstractDefinedFunction(int adfId, Type type) {
      this.adfId = adfId;
      this.type = type;
   }

   @Override
   public <T> T evaluate(Assignments assignments, AbstractDefinedFunctions adfs) {
      return adfs.getAbstractDefinedFunction(adfId).evaluate(assignments, adfs);
   }

   @Override
   public Type getType() {
      return type;
   }

   @Override
   public NodeType getNodeType() {
      return NodeType.CONSTANT;
   }
}
