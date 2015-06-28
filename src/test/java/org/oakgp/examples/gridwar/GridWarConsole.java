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
package org.oakgp.examples.gridwar;

import org.oakgp.TestUtils;
import org.oakgp.util.JavaUtilRandomAdapter;

/** Console application to allow a human player to play GridWar against a computer opponent. */
public class GridWarConsole {
   public static void main(String[] args) {
      // simpler example: (+ (* (- v0 3) (+ v2 v2)) (+ 1 v2))
      String opponent = "(if (< v2 0) (- (+ (+ (* v4 v0) (+ (* (- v0 v2) v2) v3)) v2) (* 3 v0)) (- (* (* v3 (+ v1 v3)) (- (- (* v2 v2) v2) (- (* (+ v3 (+ (- (+ v2 (- (+ 2 v2) (* (* v2 v3) (+ (- (* 2 (* v3 (+ (- (* v2 4) 3) v2))) v1) v4)))) v3) (- 3 (- v1 (* 3 v0))))) 4) 2))) (if (= (+ (* v2 v1) v2) (- v0 (+ v0 v3))) 3 (- 3 v2))))";
      new GridWar(new JavaUtilRandomAdapter()).evaluate(new Human(), TestUtils.readNode(opponent));
   }
}
