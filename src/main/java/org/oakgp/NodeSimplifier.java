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

import static org.oakgp.Arguments.createArguments;
import static org.oakgp.node.NodeType.isConstant;
import static org.oakgp.node.NodeType.isFunction;

import java.util.HashSet;
import java.util.Set;

import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

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
    * (e.g. replacing {@code (if (< 2 3) (+ v0 v1) (* v0 v1)) with {@code (+ v0 v1)}.
    *
    * @param input
    *           the node to attempt to simplify.
    * @return the result of attempting to simplify {@code input}.
    * @see org.oakgp.function.Function#simplify(Arguments)
    */
   public static Node simplify(Node input) {
      int ctr = 0;
      Set<Node> s = new HashSet<>();
      Node previous;
      Node output = input;
      do {
         previous = output;
         output = simplifyOnce(output);

         // To avoid getting stuck in an infinite loop:
         // 1. exit if the result of an attempt to simplify equals the result of an earlier simplify
         if (!output.equals(previous) && !s.add(output)) {
            return output;
         }
         // 2. if the number of simplifies exceeds a defined limit then throw an exception
         if (ctr++ > MAX_RETRIES) {
            throw new IllegalArgumentException(input.toString());
         }
      } while (isFunction(output) && !output.equals(previous));
      return output;
   }

   private static Node simplifyOnce(Node input) {
      if (isFunction(input)) {
         return simplifyFunctionNode((FunctionNode) input);
      } else {
         return input;
      }
   }

   private static Node simplifyFunctionNode(final FunctionNode input) {
      // TODO it may be beneficial to add a "isSimplified" flag to FunctionNode to indicate that if it has already been simplified (to avoid trying again here)

      // try to simplify each of the arguments
      Arguments inputArgs = input.getArguments();
      Node[] simplifiedArgs = new Node[inputArgs.getArgCount()];
      boolean haveAnyArgumentsBeenSimplified = false;
      boolean areAllArgumentsConstants = true;
      for (int i = 0; i < simplifiedArgs.length; i++) {
         Node originalArg = inputArgs.getArg(i);
         simplifiedArgs[i] = simplifyOnce(originalArg);
         if (originalArg != simplifiedArgs[i]) {
            haveAnyArgumentsBeenSimplified = true;
         }
         if (!isConstant(simplifiedArgs[i])) {
            areAllArgumentsConstants = false;
         }
      }

      // if could simplify arguments then use simplified version to create new FunctionNode
      Arguments arguments;
      FunctionNode output;
      if (haveAnyArgumentsBeenSimplified) {
         arguments = createArguments(simplifiedArgs);
         output = new FunctionNode(input.getFunction(), arguments);
      } else {
         arguments = inputArgs;
         output = input;
      }

      // if a function is pure and all its arguments are constants then
      // the result of evaluating it will always be the same -
      // so, to avoid unnecessary computation and to reduce bloat,
      // the function node can be replaced with the result of evaluating it
      if (areAllArgumentsConstants && input.getFunction().isPure()) {
         return new ConstantNode(output.evaluate(null), output.getType());
      }

      // try to simplify using function specific logic
      Node simplifiedByFunctionVersion = input.getFunction().simplify(arguments);
      if (simplifiedByFunctionVersion == null) {
         return output;
      } else {
         return simplifiedByFunctionVersion;
      }
   }
}
