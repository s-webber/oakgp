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

import static org.oakgp.examples.ant.AntMovement.LEFT;
import static org.oakgp.examples.ant.AntMovement.RIGHT;
import static org.oakgp.examples.ant.AntMovement.areAllSame;
import static org.oakgp.examples.ant.AntMovement.isLeftAndRight;
import static org.oakgp.util.Void.VOID_TYPE;
import static org.oakgp.util.Void.isVoid;

import org.oakgp.Assignments;
import org.oakgp.function.ImpureFunction;
import org.oakgp.function.Signature;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.util.Void;

/** Executes three nodes in sequence. */
class TriSequence implements ImpureFunction {
   static final TriSequence TRISEQUENCE = new TriSequence();

   private TriSequence() {
   }

   @Override
   public Signature getSignature() {
      return Signature.createSignature(VOID_TYPE, VOID_TYPE, VOID_TYPE, VOID_TYPE);
   }

   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments) {
      arguments.first();
      arguments.second();
      arguments.third();
      return Void.VOID;
   }

   @Override
   public Node simplify(FunctionNode functionNode) {
      ChildNodes children = functionNode.getChildren();
      Node first = children.first();
      Node second = children.second();
      Node third = children.third();
      if (isVoid(first)) {
         return createBiSequence(second, third);
      } else if (isVoid(second)) {
         return createBiSequence(first, third);
      } else if (isVoid(third)) {
         return createBiSequence(first, second);
      } else if (isLeftAndRight(first, second)) {
         return third;
      } else if (isLeftAndRight(second, third)) {
         return first;
      } else if (areAllSame(LEFT, first, second, third)) {
         return new FunctionNode(RIGHT, VOID_TYPE, ((FunctionNode) first).getChildren());
      } else if (areAllSame(RIGHT, first, second, third)) {
         return new FunctionNode(LEFT, VOID_TYPE, ((FunctionNode) first).getChildren());
      } else {
         return null;
      }
   }

   private Node createBiSequence(Node arg1, Node arg2) {
      return new FunctionNode(BiSequence.BISEQUENCE, VOID_TYPE, arg1, arg2);
   }
}
