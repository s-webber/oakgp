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
package org.oakgp.util;

import static java.lang.Boolean.TRUE;
import static org.oakgp.TestUtils.assertContains;
import static org.oakgp.TestUtils.assertEmpty;
import static org.oakgp.TestUtils.assertUnmodifiable;
import static org.oakgp.TestUtils.booleanConstant;
import static org.oakgp.TestUtils.doubleConstant;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.comparableType;
import static org.oakgp.type.CommonTypes.doubleType;
import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.numberType;
import static org.oakgp.type.CommonTypes.stringType;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.oakgp.TestUtils;
import org.oakgp.node.ConstantNode;
import org.oakgp.type.TypeBuilder;
import org.oakgp.type.Types.Type;

public class TypeMapTest {
   @Test
   public void testGetByType() {
      Type integerSubType = TypeBuilder.name(TestUtils.uniqueTypeName()).parents(integerType()).build();
      Type integerSubTypeSubType = TypeBuilder.name(TestUtils.uniqueTypeName()).parents(integerSubType).build();
      ConstantNode c0 = integerConstant(1);
      ConstantNode c1 = booleanConstant(TRUE);
      ConstantNode c2 = integerConstant(2);
      ConstantNode c3 = doubleConstant(3);
      ConstantNode c4 = new ConstantNode(4, integerSubType);
      ConstantNode c5 = new ConstantNode(5, integerSubTypeSubType);

      TypeMap<ConstantNode> s = new TypeMap<>(Arrays.asList(c0, c1, c2, c3, c4, c5), ConstantNode::getType);

      assertContains(s.getByType(booleanType()), c1);
      assertContains(s.getByType(integerType()), c0, c2, c4, c5);
      assertContains(s.getByType(doubleType()), c3);
      assertContains(s.getByType(numberType()), c0, c2, c3, c4, c5);
      assertContains(s.getByType(comparableType()), c0, c1, c2, c3, c4, c5);
      assertContains(s.getByType(integerSubType), c4, c5);
      assertContains(s.getByType(integerSubTypeSubType), c5);

      assertEmpty(s.getByType(stringType()));
   }

   @Test
   public void assertGetByTypeUnmodifiable() {
      List<ConstantNode> constants = Arrays.asList(new ConstantNode(1, integerType()));
      TypeMap<ConstantNode> map = new TypeMap<>(constants, ConstantNode::getType);
      List<ConstantNode> integers = map.getByType(integerType());
      assertUnmodifiable(integers);
   }
}
