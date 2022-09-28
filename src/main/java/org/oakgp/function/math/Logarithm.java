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
package org.oakgp.function.math;

import static org.oakgp.type.CommonTypes.doubleType;
import static org.oakgp.type.CommonTypes.numberType;

import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.AbstractDefinedFunctions;
import org.oakgp.node.ChildNodes;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;

/**
 * Calculates the natural logarithm of a number.
 *
 * @see java.lang.Math#log(double)
 */
public final class Logarithm implements Function {
   private static final Logarithm SINGLETON = new Logarithm();

   public static Logarithm getSingleton() {
      return SINGLETON;
   }

   private final Signature signature;

   private Logarithm() {
      Type element = Types.generic("Element", numberType());
      signature = Signature.createSignature(doubleType(), element);
   }

   @Override
   public Double evaluate(ChildNodes arguments, Assignments assignments, AbstractDefinedFunctions adfs) {
      Number a = arguments.first().evaluate(assignments, adfs);
      return Math.log(a.doubleValue());
   }

   @Override
   public Signature getSignature() {
      return signature;
   }

   @Override
   public String getDisplayName() {
      return "log";
   }
}
