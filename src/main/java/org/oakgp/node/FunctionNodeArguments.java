/*
 * Copyright 2019 S. Webber
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
package org.oakgp.node;

import org.oakgp.Arguments;
import org.oakgp.Assignments;

/**
 * Uses the children of a {@link FunctionNode} as the arguments to a function.
 * <p>
 * Immutable.
 */
final class FunctionNodeArguments implements Arguments {
   private final ChildNodes childNodes;
   private final Assignments assignments;

   public FunctionNodeArguments(ChildNodes childNodes, Assignments assignments) {
      this.childNodes = childNodes;
      this.assignments = assignments;
   }

   @Override
   public <T> T getArg(int index) {
      return childNodes.getNode(index).evaluate(assignments);
   }

   @Override
   public int size() {
      return childNodes.size();
   }
}
