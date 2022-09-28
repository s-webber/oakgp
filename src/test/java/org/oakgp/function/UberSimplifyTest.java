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
package org.oakgp.function;

import static org.junit.Assert.assertEquals;
import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.integerType;

import org.junit.Test;
import org.oakgp.Assignments;
import org.oakgp.NodeSimplifier;
import org.oakgp.function.bool.And;
import org.oakgp.function.bool.Or;
import org.oakgp.function.bool.Xor;
import org.oakgp.function.choice.If;
import org.oakgp.function.classify.IsEven;
import org.oakgp.function.classify.IsNegative;
import org.oakgp.function.classify.IsOdd;
import org.oakgp.function.classify.IsPositive;
import org.oakgp.function.classify.IsZero;
import org.oakgp.function.compare.Equal;
import org.oakgp.function.compare.GreaterThan;
import org.oakgp.function.compare.GreaterThanOrEqual;
import org.oakgp.function.compare.NotEqual;
import org.oakgp.function.math.IntegerUtils;
import org.oakgp.generate.TreeGenerator;
import org.oakgp.generate.TreeGeneratorImpl;
import org.oakgp.node.Node;
import org.oakgp.primitive.ConstantSet;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.primitive.PrimitiveSet;
import org.oakgp.primitive.PrimitiveSetImpl;
import org.oakgp.primitive.VariableSet;
import org.oakgp.util.ConstantSetBuilder;
import org.oakgp.util.FunctionSetBuilder;
import org.oakgp.util.JavaUtilRandomAdapter;

public class UberSimplifyTest {
   @Test
   public void test() {
      Assignments[] assignments = new Assignments[] { //
            Assignments.createAssignments(0, 0, 0, true, true, true), //
            Assignments.createAssignments(1, 2, 3, true, true, false), //
            Assignments.createAssignments(3, 6, 18, true, false, true), //
            Assignments.createAssignments(-5, -7, -6, false, true, true), //
            Assignments.createAssignments(1, 0, -3, true, false, false), //
            Assignments.createAssignments(7, 2, 0, false, true, false), //
            Assignments.createAssignments(-1, 1, 2, false, false, true), //
            Assignments.createAssignments(5, 2, 3, false, false, false), //
      };
      PrimitiveSet primitiveSet = createPrimitiveSet();
      TreeGenerator g = TreeGeneratorImpl.full(primitiveSet);
      for (int i = 1; i < 100; i++) {
         Node original = g.generate(booleanType(), 3);
         Node simplified = NodeSimplifier.simplify(original);
         System.out.println(i + ". " + original.getNodeCount() + " v " + simplified.getNodeCount());
         for (Assignments a : assignments) {
            Object originalResult = original.evaluate(a, null);
            Object simplifiedResult = simplified.evaluate(a, null);
            assertEquals(original.toString(), originalResult, simplifiedResult);
         }
      }
   }

   private PrimitiveSet createPrimitiveSet() {
      If i = new If();
      FunctionSet functions = new FunctionSetBuilder() //
            .add(i, booleanType()) //
            .add(Equal.getSingleton(), booleanType()) //
            .add(NotEqual.getSingleton(), booleanType()) //
            .add(i, integerType()) //
            .add(Equal.getSingleton(), integerType()) //
            .add(NotEqual.getSingleton(), integerType()) //
            .add(GreaterThan.getSingleton(), integerType()) //
            .add(GreaterThanOrEqual.getSingleton(), integerType()) //
            .add(IsNegative.getSingleton()) //
            .add(IsPositive.getSingleton()) //
            .add(IsOdd.getSingleton()) //
            .add(IsEven.getSingleton()) //
            .add(IsZero.getSingleton()) //
            .add(And.getSingleton()) //
            .add(Or.getSingleton()) //
            .add(Xor.getSingleton()) //
            .add(IntegerUtils.INTEGER_UTILS.getAdd()) //
            .add(IntegerUtils.INTEGER_UTILS.getMultiply()) //
            .add(IntegerUtils.INTEGER_UTILS.getSubtract()) //
            .build();
      ConstantSet constants = new ConstantSetBuilder().integerRange(-5, 5).build();
      VariableSet variables = VariableSet.createVariableSet(integerType(), integerType(), integerType(), booleanType(), booleanType(), booleanType());
      PrimitiveSet primitiveSet = new PrimitiveSetImpl(functions, constants, variables, new JavaUtilRandomAdapter(), .7);
      return primitiveSet;
   }
}
