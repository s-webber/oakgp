package org.oakgp.tournament;

import org.oakgp.node.Node;

/** Represents a two-player zero-sum game. */
public interface TwoPlayerGame {
	double evaluate(Node player1, Node player2);
}
