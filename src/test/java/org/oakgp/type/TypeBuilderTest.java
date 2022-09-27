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

import static org.oakgp.TestUtils.uniqueType;
import static org.oakgp.TestUtils.uniqueTypeName;
import static org.oakgp.type.TypeAssertions.assertName;
import static org.oakgp.type.TypeAssertions.assertParameters;
import static org.oakgp.type.TypeAssertions.assertParents;

import org.junit.Test;
import org.oakgp.type.Types.Type;

public class TypeBuilderTest {
   @Test
   public void createByString() {
      String expectedName = uniqueTypeName();
      Type b = uniqueType();
      Type c = uniqueType();
      Type d = uniqueType();
      Type e = uniqueType();
      Type type = TypeBuilder.name(expectedName).parameters(b, b).parents(c, d, e).build();

      assertName(type, expectedName);
      assertParameters(type, b, b);
      assertParents(type, c, d, e);
   }
}
