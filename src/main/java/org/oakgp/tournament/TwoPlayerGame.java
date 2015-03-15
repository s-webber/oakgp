package org.oakgp.tournament;

import org.oakgp.node.Node;

public interface TwoPlayerGame {
	double evaluate(Node player1, Node player2);
}
