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

import static org.junit.Assert.assertEquals;
import static org.oakgp.TestUtils.longConstant;
import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerType;
import static org.oakgp.Type.stringType;

import org.junit.Test;
import org.oakgp.Assignments;
import org.oakgp.primitive.VariableSet;

public class FunctionNodeArgumentsTest {
   @Test
   public void test() {
      Boolean firstArg = true;
      Long secondArg = 72L;
      String thirdArg = "test";
      Integer fourthArg = 42;
      VariableSet variableSet = VariableSet.createVariableSet(integerType(), booleanType(), stringType());
      ChildNodes childNodes = ChildNodes.createChildNodes(variableSet.getById(1), longConstant(secondArg), variableSet.getById(2), variableSet.getById(0));
      Assignments assignments = Assignments.createAssignments(fourthArg, firstArg, thirdArg);

      FunctionNodeArguments functionNodeArguments = new FunctionNodeArguments(childNodes, assignments);

      assertEquals(4, functionNodeArguments.size());
      assertEquals(firstArg, functionNodeArguments.first());
      assertEquals(firstArg, functionNodeArguments.getArg(0));
      assertEquals(secondArg, functionNodeArguments.second());
      assertEquals(secondArg, functionNodeArguments.getArg(1));
      assertEquals(thirdArg, functionNodeArguments.third());
      assertEquals(thirdArg, functionNodeArguments.getArg(2));
      assertEquals(fourthArg, functionNodeArguments.getArg(3));
   }
}
