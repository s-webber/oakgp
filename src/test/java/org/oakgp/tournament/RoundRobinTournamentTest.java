package org.oakgp.tournament;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.oakgp.TestUtils.assertRankedCandidate;
import static org.oakgp.TestUtils.createConstant;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.oakgp.GenerationProcessor;
import org.oakgp.RankedCandidate;
import org.oakgp.node.Node;

public class RoundRobinTournamentTest {
   @Test
   public void test() {
      // test data
      Node a = createConstant(1);
      Node b = createConstant(2);
      Node c = createConstant(3);
      List<Node> input = Arrays.asList(a, b, c);

      // mock
      TwoPlayerGame mockGame = mock(TwoPlayerGame.class);
      given(mockGame.evaluate(a, b)).willReturn(5d);
      given(mockGame.evaluate(a, c)).willReturn(-3d);
      given(mockGame.evaluate(b, c)).willReturn(2d);

      // invoke process method
      GenerationProcessor tournament = new RoundRobinTournament(mockGame);
      List<RankedCandidate> output = tournament.process(input);

      // assert output
      assertRankedCandidate(output.get(0), a, 2);
      assertRankedCandidate(output.get(1), c, 1);
      assertRankedCandidate(output.get(2), b, -3);
   }
}
