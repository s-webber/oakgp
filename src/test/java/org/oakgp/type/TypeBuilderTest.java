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
package org.oakgp.type;

import static org.oakgp.TestUtils.uniqueTypeName;
import static org.oakgp.type.CommonTypes.bigDecimalType;
import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.comparableType;
import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.numberType;
import static org.oakgp.type.CommonTypes.stringType;
import static org.oakgp.type.TypeAssertions.assertName;
import static org.oakgp.type.TypeAssertions.assertNoParameters;
import static org.oakgp.type.TypeAssertions.assertNoParents;
import static org.oakgp.type.TypeAssertions.assertParameters;
import static org.oakgp.type.TypeAssertions.assertParents;

import org.junit.Test;
import org.oakgp.type.Types.Type;

public class TypeBuilderTest {
   @Test
   public void createByClass() {
      Type type = TypeBuilder.name(TypeBuilderTest.class).build();

      assertName(type, "TypeBuilderTest");
      assertNoParameters(type);
      assertNoParents(type);
   }

   @Test
   public void createByString() {
      String expectedName = uniqueTypeName();
      Type type = TypeBuilder.name(expectedName).parameters(booleanType(), booleanType()).parents(bigDecimalType(), stringType(), integerType()).build();

      assertName(type, expectedName);
      assertParameters(type, booleanType(), booleanType());
      assertParents(type, bigDecimalType(), stringType(), integerType(), numberType(), comparableType());
   }
}
