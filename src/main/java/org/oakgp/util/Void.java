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
package org.oakgp.util;

import org.oakgp.Type;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.node.NodeType;

/** Represents a dummy value to return from functions that do not produce a result. */
public class Void {
   /** Returns the type associated with instances of {@link #VOID}. */
   public static final Type VOID_TYPE = Type.type("void");
   /** Singleton representing a {@code void} value. */
   public static final Void VOID = new Void();
   /** A constant node containing {@link #VOID}. */
   public static final ConstantNode VOID_CONSTANT = new ConstantNode(VOID, VOID_TYPE);

   /** Private constructor to force use of {@link #VOID} */
   private Void() {
   }

   /** Returns {@code true} if the given node is a constant node containing {@link #VOID}. */
   public static boolean isVoid(Node n) {
      return NodeType.isConstant(n) && n.evaluate(null) == Void.VOID;
   }

   @Override
   public String toString() {
      return "void";
   }
}
