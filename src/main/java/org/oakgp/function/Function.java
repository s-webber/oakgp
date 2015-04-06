package org.oakgp.function;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.node.Node;

/** Represents an operation. */
public interface Function {
   /**
    * Returns the result of applying this operation to the specified {@code Arguments} and {@code Assignments}.
    *
    * @param arguments
    *           represents the arguments to apply to the operation
    * @param assignments
    *           represents values assigned to variables belonging to {@code arguments}
    * @return the result of applying this operation to the {@code arguments} and {@code assignments}
    */
   Object evaluate(Arguments arguments, Assignments assignments);

   Signature getSignature();

   default Node simplify(Arguments arguments) {
      return null;
   }

   default String getDisplayName() {
      String className = getClass().getName();
      int packagePos = className.lastIndexOf('.');
      String lowerCaseNameMinusPackage = className.substring(packagePos + 1).toLowerCase();
      if (lowerCaseNameMinusPackage.startsWith("is") && getSignature().getReturnType() == Type.booleanType()) {
         return lowerCaseNameMinusPackage.substring(2) + "?";
      } else {
         return lowerCaseNameMinusPackage;
      }
   }
}
