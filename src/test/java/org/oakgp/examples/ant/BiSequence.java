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
package org.oakgp.examples.ant;

import static org.oakgp.examples.ant.AntMovement.isLeftAndRight;
import static org.oakgp.util.Void.VOID_CONSTANT;
import static org.oakgp.util.Void.VOID_TYPE;
import static org.oakgp.util.Void.isVoid;

import org.oakgp.Assignments;
import org.oakgp.function.ImpureFunction;
import org.oakgp.function.Signature;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

/** Executes two nodes in sequence. */
class BiSequence implements ImpureFunction {
   static final BiSequence BISEQUENCE = new BiSequence();

   private BiSequence() {
   }

   @Override
   public Signature getSignature() {
      return Signature.createSignature(VOID_TYPE, VOID_TYPE, VOID_TYPE);
   }

   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments) {
      arguments.first().evaluate(assignments);;
      arguments.second().evaluate(assignments);;
      return null;
   }

   @Override
   public Node simplify(FunctionNode functionNode) {
      ChildNodes children = functionNode.getChildren();
      Node firstArg = children.first();
      Node secondArg = children.second();
      if (isVoid(firstArg)) {
         return secondArg;
      } else if (isVoid(secondArg)) {
         return firstArg;
      } else if (isLeftAndRight(firstArg, secondArg)) {
         return VOID_CONSTANT;
      } else if (isBiSequence(firstArg)) {
         ChildNodes firstArgChildren = ((FunctionNode) firstArg).getChildren();
         return createTriSequence(firstArgChildren.first(), firstArgChildren.second(), secondArg);
      } else if (isBiSequence(secondArg)) {
         ChildNodes secondArgChildren = ((FunctionNode) secondArg).getChildren();
         return createTriSequence(firstArg, secondArgChildren.first(), secondArgChildren.second());
      } else {
         return null;
      }
   }

   private boolean isBiSequence(Node firstArg) {
      FunctionNode fn = (FunctionNode) firstArg;
      return fn.getFunction() == BISEQUENCE;
   }

   private Node createTriSequence(Node arg1, Node arg2, Node arg3) {
      return new FunctionNode(TriSequence.TRISEQUENCE, VOID_TYPE, arg1, arg2, arg3);
   }
}
