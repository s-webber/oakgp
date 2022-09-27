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
package org.oakgp.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.oakgp.type.CommonTypes.integerType;

import org.junit.Test;
import org.oakgp.node.ConstantNode;
import org.oakgp.type.CommonTypes;
import org.oakgp.type.Types;

public class VoidTest {
   @Test
   public void testConstantNode() {
      assertSame(Void.VOID_TYPE, Void.VOID_CONSTANT.getType());
      assertSame(Void.class, Void.VOID_CONSTANT.evaluate(null).getClass());
   }

   @Test
   public void testIsVoid() {
      assertTrue(Void.isVoid(Void.VOID_CONSTANT));
      assertTrue(Void.isVoid(new ConstantNode(null, CommonTypes.voidType())));
   }

   @Test
   public void testIsNotVoid() {
      assertFalse(Void.isVoid(new ConstantNode(null, Types.declareType("dummy"))));
      assertFalse(Void.isVoid(new ConstantNode(1, integerType())));
   }

   @Test
   public void testToString() {
      assertEquals("void", Void.VOID_CONSTANT.evaluate(null).toString());
   }
}
