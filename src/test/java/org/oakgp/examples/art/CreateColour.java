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
package org.oakgp.examples.art;

import static org.oakgp.type.CommonTypes.integerType;

import java.awt.Color;

import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.ChildNodes;

public class CreateColour implements Function {
   private static final int RANGE = 256;
   private static final Signature SIGNATURE = Signature.createSignature(ArtFactory.COLOUR_TYPE, integerType(), integerType(), integerType());

   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments) {
      int red = arguments.first().evaluate(assignments);
      int green = arguments.second().evaluate(assignments);
      int blue = arguments.third().evaluate(assignments);
      return new Color(normalise(red), normalise(green), normalise(blue));
   }

   private int normalise(int component) {
      return Math.abs(component) % RANGE;
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }
}
