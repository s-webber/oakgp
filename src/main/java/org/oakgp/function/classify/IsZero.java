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

/** Determines if a number is zero. */
public final class IsZero implements BooleanFunction {
   private static final Signature SIGNATURE = Signature.createSignature(booleanType(), integerType());
   private static final IsZero SINGLETON = new IsZero();

   public static IsZero getSingleton() {
      return SINGLETON;
   }

   private IsZero() {
   }

   @Override
   public Object evaluate(Arguments arguments) {
      int i = arguments.first();
      return i == 0;
   }

   @Override
   public RulesEngine getEngine(FunctionNode fn) {
      RulesEngine e = new RulesEngine();
      e.addRule(fn, (_e, f, v) -> {
         _e.addFact(new FunctionNode(IsEven.getSingleton(), fn.getType(), fn.getChildren()), v);
         _e.addFact(new FunctionNode(IsOdd.getSingleton(), fn.getType(), fn.getChildren()), !v);

         FunctionNode positive = new FunctionNode(IsPositive.getSingleton(), fn.getType(), fn.getChildren());
         FunctionNode negative = new FunctionNode(IsNegative.getSingleton(), fn.getType(), fn.getChildren());
         if (v) {
            _e.addFact(positive, false);
            _e.addFact(negative, false);
         } else {
            if (_e.hasFact(positive)) {
               _e.addFact(negative, !_e.getFact(positive));
            }
            if (_e.hasFact(negative)) {
               _e.addFact(positive, !_e.getFact(negative));
            }
         }
      });
      return e;
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }
}
