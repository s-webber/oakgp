package org.oakgp.examples.tictactoe;

enum Symbol {
   X, O;

   Symbol getOpponent() {
      return this == X ? O : X;
   }
}
