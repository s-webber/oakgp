package org.oakgp.examples.gridwar;

import org.oakgp.TestUtils;
import org.oakgp.util.JavaUtilRandomAdapter;

/** Console application to allow a human player to play GridWar against a computer opponent. */
public class GridWarConsole {
	public static void main(String[] args) {
		String opponent = "(+ (* (- v0 3) (+ v2 v2)) (+ 1 v2))";
		new GridWar(new JavaUtilRandomAdapter()).evaluate(new Human(), TestUtils.readNode(opponent));
	}
}
