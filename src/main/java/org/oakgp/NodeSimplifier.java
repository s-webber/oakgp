/*
 * Copyright 2015 S. Webber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oakgp;

import static org.oakgp.node.ChildNodes.createChildNodes;
import static org.oakgp.node.NodeType.isConstant;
import static org.oakgp.node.NodeType.isFunction;

import java.util.HashSet;
import java.util.Set;

import org.oakgp.node.ChildNodes;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.ProgramNode;

/**
 * Attempts to reduce the size of tree structures without altering their functionality.
 * <p>
 * This can be done by replacing expressions with constant values or removing redundant branches. e.g. The expression:
 *
 * <pre>
 * (+ 7 (* 3 6))
 * </pre>
 *
 * can be simplified to the value:
 *
 * <pre>
 * 25
 * </pre>
 *
 * <b>Note:</b> relies on {@link org.oakgp.function.Function#isPure()} to identify if a function is referentially transparent and therefore suitable for
 * replacement with the result of evaluating it.
 */
public final class NodeSimplifier {
   private static final int MAX_RETRIES = 100;

   /** Private constructor as all methods are static. */
   private NodeSimplifier() {
      // do nothing
   }

   /**
    * Attempts to reduce the size of the specified tree structures without altering its functionality.
    * <p>
    * Simplification can occur by replacing expressions with constant values (e.g. replacing {@code (+ 1 1)} with {@code 2}) or removing redundant branches
    * (e.g. replacing {@code (if (< 2 3) (+ v0 v1) (* v0 v1))} with {@code (+ v0 v1)}.
    *
    * @param input
    *           the node to attempt to simplify.
    * @return the result of attempting to simplify {@code input}.
    * @see org.oakgp.function.Function#simplify(FunctionNode)
    */
   public static Node simplify(Node input) {
      if (input.isSimplified()) {
         return input;
      }

      int ctr = 0;
      Set<Node> s = new HashSet<>();
      Node previous;
      Node output = input;

      // long start = System.currentTimeMillis();
      do {
         previous = output;
         output = simplifyOnce(output);

         // To avoid getting stuck in an infinite loop:
         // 1. exit if the result of an attempt to simplify equals the result of an earlier simplify
         if (!output.equals(previous) && !s.add(output)) { // TODO just "if (!s.add(output))"?
            break;
         }
         // 2. if the number of simplifies exceeds a defined limit then throw an exception
         if (ctr++ > MAX_RETRIES) {
            throw new IllegalArgumentException(input.toString());
         }
      } while (output.getNodeCount() > 1 && !output.equals(previous));
      // long took = System.currentTimeMillis() - start;
      // if (took > 2000) {
      // System.out.println(">>> " + input.getNodeCount() + " " + input);
      // System.out.println("<<< " + output.getNodeCount() + " " + output);
      // System.exit(1);
      // }
      output.setSimplified();
      return output;
   }

   private static Node simplifyOnce(Node input) {
      if (input instanceof ProgramNode) {
         return simplifyProgramNode((ProgramNode) input);
      } else if (isFunction(input)) {
         return simplifyFunctionNode((FunctionNode) input);
      } else {
         return input;
      }
   }

   private static Node simplifyProgramNode(final ProgramNode originalProgramNode) {
      Node[] newBranches = new Node[originalProgramNode.adfCount()];
      boolean updated = false;
      for (int i = 0; i < newBranches.length; i++) {
         Node originalBranch = originalProgramNode.getAbstractDefinedFunction(i);
         if (originalBranch != (newBranches[i] = simplify(originalBranch))) {
            updated = true;
         }
      }
      return updated ? new ProgramNode(newBranches) : originalProgramNode;
   }

   private static Node simplifyFunctionNode(final FunctionNode input) {
      // try to simplify each of the arguments
      ChildNodes inputChildren = input.getChildren();
      Node[] simplifiedArgs = new Node[inputChildren.size()];
      boolean haveAnyArgumentsBeenSimplified = false;
      boolean areAllArgumentsConstants = true;
      for (int i = 0; i < simplifiedArgs.length; i++) {
         Node originalArg = inputChildren.getNode(i);
         simplifiedArgs[i] = simplify(originalArg);
         if (originalArg != simplifiedArgs[i]) {
            haveAnyArgumentsBeenSimplified = true;
         }
         if (!isConstant(simplifiedArgs[i])) {
            areAllArgumentsConstants = false;
         }
      }

      // if could simplify arguments then use simplified version to create new FunctionNode
      FunctionNode output;
      if (haveAnyArgumentsBeenSimplified) {
         output = new FunctionNode(input, createChildNodes(simplifiedArgs));
      } else {
         output = input;
      }

      // if a function is pure and all its arguments are constants then
      // the result of evaluating it will always be the same -
      // so, to avoid unnecessary computation and to reduce bloat,
      // the function node can be replaced with the result of evaluating it
      if (areAllArgumentsConstants && input.getFunction().isPure()) {
         return new ConstantNode(output.evaluate(null, null), output.getType());
      }

      // try to simplify using function specific logic
      Node simplifiedByFunctionVersion = input.getFunction().simplify(output);
      if (simplifiedByFunctionVersion == null) {
         return output;
      } else {
         return simplifiedByFunctionVersion;
      }
   }
}
