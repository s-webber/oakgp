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
