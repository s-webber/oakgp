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
package org.oakgp.function.classify;

import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.integerType;

import org.oakgp.Arguments;
import org.oakgp.function.BooleanFunction;
import org.oakgp.function.RulesEngine;
import org.oakgp.function.Signature;
import org.oakgp.node.FunctionNode;

/** Determines if a number is negative. */
public final class IsNegative implements BooleanFunction {
   private static final Signature SIGNATURE = Signature.createSignature(booleanType(), integerType());
   private static final IsNegative SINGLETON = new IsNegative();

   public static IsNegative getSingleton() {
      return SINGLETON;
   }

   private IsNegative() {
   }

   @Override
   public Object evaluate(Arguments arguments) {
      int i = arguments.first();
      return i < 0;
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }

   @Override
   public RulesEngine getEngine(FunctionNode fn) {
      RulesEngine e = new RulesEngine();
      e.addRule(fn, (_e, f, v) -> {
         FunctionNode positive = new FunctionNode(IsPositive.getSingleton(), fn.getType(), fn.getChildren());
         FunctionNode zero = new FunctionNode(IsZero.getSingleton(), fn.getType(), fn.getChildren());
         if (v) {
            _e.addFact(positive, false);
            _e.addFact(zero, false);
         } else {
            if (_e.hasFact(positive)) {
               _e.addFact(zero, !_e.getFact(positive));
            }
            if (_e.hasFact(zero)) {
               _e.addFact(positive, !_e.getFact(zero));
            }
         }
      });
      return e;
   }

   @Override
   public String getDisplayName() {
      return "neg?";
   }
}
