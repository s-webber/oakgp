package org.oakgp.crossover;

import org.oakgp.Arguments;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

final class CommonRegion {
   /** Private constructor as all methods are static. */
   private CommonRegion() {
      // do nothing
   }

   static Node crossoverAt(Node n1, Node n2, int crossOverPoint) {
      boolean isFirstFunction = n1 instanceof FunctionNode;
      boolean isSecondFunction = n2 instanceof FunctionNode;
      if (isFirstFunction && isSecondFunction) {
         FunctionNode f1 = (FunctionNode) n1;
         FunctionNode f2 = (FunctionNode) n2;
         Arguments arguments = f1.getArguments();
         int argCount = arguments.getArgCount();
         if (argCount == f2.getArguments().getArgCount()) {
            int total = 0;
            for (int i = 0; i < argCount; i++) {
               Node a1 = arguments.getArg(i);
               Node a2 = f2.getArguments().getArg(i);
               int c = getNodeCount(a1, a2);
               if (total + c > crossOverPoint) {
                  return new FunctionNode(f1.getFunction(), arguments.replaceAt(i, crossoverAt(a1, a2, crossOverPoint - total)));
               } else {
                  total += c;
               }
            }
         }
      }

      return sameType(n1, n2) ? n2 : n1;
   }

   static int getNodeCount(Node n1, Node n2) {
      boolean isFirstFunction = n1 instanceof FunctionNode;
      boolean isSecondFunction = n2 instanceof FunctionNode;
      if (isFirstFunction && isSecondFunction) {
         int total = sameType(n1, n2) ? 1 : 0;
         FunctionNode f1 = (FunctionNode) n1;
         FunctionNode f2 = (FunctionNode) n2;
         int argCount = f1.getArguments().getArgCount();
         if (argCount == f2.getArguments().getArgCount()) {
            for (int i = 0; i < argCount; i++) {
               total += getNodeCount(f1.getArguments().getArg(i), f2.getArguments().getArg(i));
            }
         }
         return total;
      } else if (!isFirstFunction && !isSecondFunction) {
         // both terminal nodes
         return sameType(n1, n2) ? 1 : 0;
      } else {
         // terminal node does not match with a function node
         return 0;
      }
   }

   private static boolean sameType(Node n1, Node n2) {
      return n1.getType() == n2.getType();
   }
}
