package org.oakgp.tournament;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.oakgp.TestUtils.createConstant;

import org.junit.Test;
import org.oakgp.node.Node;

public class FirstPlayerAdvantageGameTest {
	@Test
	public void test() {
		Node n1 = createConstant(1);
		Node n2 = createConstant(2);
		TwoPlayerGame mockTwoPlayerGame = mock(TwoPlayerGame.class);
		given(mockTwoPlayerGame.evaluate(n1, n2)).willReturn(7d);
		given(mockTwoPlayerGame.evaluate(n2, n1)).willReturn(4d);

		FirstPlayerAdvantageGame g = new FirstPlayerAdvantageGame(mockTwoPlayerGame);

		assertEquals(3d, g.evaluate(n1, n2), 0);
		assertEquals(-3d, g.evaluate(n2, n1), 0);
	}
}
