package org.oakgp.tournament;

import org.oakgp.node.Node;

/**
 * Some games have a "first-mover advantage" - meaning the player that moves first has a greater chance of winning than the player that moves second. To avoid
 * "first-mover advantage" causing players to be unfairly ranked, each pair plays each other twice - with each having the opportunity to move first.
 */
public final class FirstPlayerAdvantageGame implements TwoPlayerGame {
	private final TwoPlayerGame twoPlayerGame;

	public FirstPlayerAdvantageGame(TwoPlayerGame twoPlayerGame) {
		this.twoPlayerGame = twoPlayerGame;
	}

	@Override
	public double evaluate(Node player1, Node player2) {
		return twoPlayerGame.evaluate(player1, player2) - twoPlayerGame.evaluate(player2, player1);
	}
}
