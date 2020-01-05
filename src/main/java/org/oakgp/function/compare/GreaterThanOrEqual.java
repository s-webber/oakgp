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
package org.oakgp.function.compare;

import org.oakgp.function.RulesEngine;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.util.NodeComparator;

/** Determines if the object represented by the first argument is greater than or equal to the object represented by the second. */
public final class GreaterThanOrEqual extends ComparisonOperator {
   private static final GreaterThanOrEqual SINGLETON = new GreaterThanOrEqual();

   public static GreaterThanOrEqual getSingleton() {
      return SINGLETON;
   }

   /** Constructs a function that compares two arguments of the specified type. */
   private GreaterThanOrEqual() {
      super(true);
   }

   @Override
   protected boolean evaluate(int diff) {
      return diff >= 0;
   }

   @Override
   public RulesEngine getEngine(FunctionNode fn) {
      RulesEngine e = new RulesEngine();

      e.addRule(fn, (_e, fact, value) -> {
         ChildNodes swappedArgs = fn.getChildren().swap(0, 1);
         ChildNodes orderedArgs = NodeComparator.NODE_COMPARATOR.compare(fn.getChildren().first(), swappedArgs.first()) < 0 ? fn.getChildren() : swappedArgs;

         _e.addFact(new FunctionNode(GreaterThan.getSingleton(), fn.getType(), swappedArgs), !value);

         if (value) {
            _e.addRule(new FunctionNode(GreaterThanOrEqual.getSingleton(), fn.getType(), swappedArgs), (__e, __fact, __value) -> {
               __e.addFact(new FunctionNode(Equal.getSingleton(), fn.getType(), orderedArgs), __value);
               __e.addFact(new FunctionNode(NotEqual.getSingleton(), fn.getType(), orderedArgs), !__value);
               __e.addFact(new FunctionNode(GreaterThan.getSingleton(), fn.getType(), fn.getChildren()), !__value);
            });
         } else {
            _e.addFact(new FunctionNode(NotEqual.getSingleton(), fn.getType(), orderedArgs), true);
            _e.addFact(new FunctionNode(Equal.getSingleton(), fn.getType(), orderedArgs), false);
            _e.addFact(new FunctionNode(GreaterThan.getSingleton(), fn.getType(), fn.getChildren()), false);
         }
      });

      return e;
   }

   @Override
   public String getDisplayName() {
      return ">=";
   }
}
