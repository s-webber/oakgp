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
package org.oakgp.function.choice;

import static org.oakgp.node.NodeType.isFunction;
import static org.oakgp.type.CommonTypes.isNullable;

import org.oakgp.Arguments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.walk.NodeWalk;
import org.oakgp.type.Types.Type;

/**
 * A selection operator that uses the value of an enum to determine which code to evaluate.
 * <p>
 * This behaviour is similar to a Java {@code switch} statement that uses an enum.
 */
public final class SwitchEnum implements Function {
   private final Signature signature;
   private final Enum<?>[] enumConstants;

   /**
    * Constructs a selection operator that returns values of the specified type.
    *
    * @param enumClass
    *           the enum to compare the first argument against in order to determine which branch to evaluate
    * @param enumType
    *           the type associated with {@code enumClass}
    * @param returnType
    *           the type associated with values returned from the evaluation of this function
    */
   public SwitchEnum(Class<? extends Enum<?>> enumClass, Type enumType, Type returnType) {
      this.enumConstants = enumClass.getEnumConstants();
      Type[] types = new Type[enumConstants.length + (isNullable(enumType) ? 2 : 1)];
      types[0] = enumType;
      for (int i = 1; i < types.length; i++) {
         types[i] = returnType;
      }
      this.signature = Signature.createSignature(returnType, types);
   }

   @Override
   public Object evaluate(Arguments arguments) {
      Enum<?> input = arguments.first();
      int index = (input == null ? enumConstants.length : input.ordinal()) + 1;
      return arguments.getArg(index);
   }

   @Override
   public Node simplify(FunctionNode functionNode) {
      Type returnType = functionNode.getType();
      ChildNodes children = functionNode.getChildren();
      // TODO this is similar to the logic in NodeWalk.replaceAll - is it possible to reuse?
      boolean updated = false;
      Node[] replacementArgs = new Node[children.size()];
      Node input = children.first();
      replacementArgs[0] = input;
      for (int i = 1; i < children.size(); i++) {
         Node arg = children.getNode(i);
         final int idx = i;
         Node replacedArg = NodeWalk.replaceAll(arg, n -> isFunction(n) && ((FunctionNode) n).getFunction() == this,
               n -> ((FunctionNode) n).getChildren().getNode(idx));
         if (arg != replacedArg) {
            updated = true;
         }
         replacementArgs[i] = replacedArg;
      }
      if (updated) {
         return new FunctionNode(this, returnType, ChildNodes.createChildNodes(replacementArgs));
      } else {
         return null;
      }
   }

   @Override
   public Signature getSignature() {
      return signature;
   }

   @Override
   public String getDisplayName() {
      return "switch";
   }
}
