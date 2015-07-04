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

import static org.oakgp.Type.isNullable;
import static org.oakgp.node.NodeType.isFunction;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

/**
 * A selection operator that uses the value of an enum to determine which code to evaluate.
 * <p>
 * This behaviour is similar to a Java {@code switch} statement that uses an enum.
 */
public final class SwitchEnum implements Function {
   private final Signature signature;
   private final Enum<?>[] enumConstants;

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
   public Object evaluate(Arguments arguments, Assignments assignments) {
      Enum<?> input = arguments.firstArg().evaluate(assignments);
      int index = (input == null ? enumConstants.length : input.ordinal()) + 1;
      return arguments.getArg(index).evaluate(assignments);
   }

   @Override
   public Node simplify(Arguments arguments) {
      // TODO share with function node replaceAll
      boolean updated = false;
      Node[] replacementArgs = new Node[arguments.getArgCount()];
      Node input = arguments.firstArg();
      replacementArgs[0] = input;
      for (int i = 1; i < arguments.getArgCount(); i++) {
         Node arg = arguments.getArg(i);
         final int idx = i;
         Node replacedArg = arg.replaceAll(n -> isFunction(n) && ((FunctionNode) n).getFunction() == this, n -> ((FunctionNode) n).getArguments().getArg(idx));
         if (arg != replacedArg) {
            updated = true;
         }
         replacementArgs[i] = replacedArg;
      }
      if (updated) {
         return new FunctionNode(this, Arguments.createArguments(replacementArgs));
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
