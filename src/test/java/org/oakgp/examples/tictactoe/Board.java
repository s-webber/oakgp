package org.oakgp.examples.tictactoe;

import static java.util.EnumSet.allOf;
import static java.util.EnumSet.copyOf;
import static java.util.EnumSet.noneOf;
import static java.util.EnumSet.of;
import static org.oakgp.examples.tictactoe.Move.BOTTOM_CENTRE;
import static org.oakgp.examples.tictactoe.Move.BOTTOM_LEFT;
import static org.oakgp.examples.tictactoe.Move.BOTTOM_RIGHT;
import static org.oakgp.examples.tictactoe.Move.CENTRE;
import static org.oakgp.examples.tictactoe.Move.MIDDLE_LEFT;
import static org.oakgp.examples.tictactoe.Move.MIDDLE_RIGHT;
import static org.oakgp.examples.tictactoe.Move.TOP_CENTRE;
import static org.oakgp.examples.tictactoe.Move.TOP_LEFT;
import static org.oakgp.examples.tictactoe.Move.TOP_RIGHT;
import static org.oakgp.examples.tictactoe.Symbol.X;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

final class Board {
   private static final int MAX_MOVES = Move.values().length;
   private static final List<EnumSet<Move>> LINES = new ArrayList<>();
   static {
      // horizontal
      LINES.add(of(TOP_LEFT, TOP_CENTRE, TOP_RIGHT));
      LINES.add(of(MIDDLE_LEFT, CENTRE, MIDDLE_RIGHT));
      LINES.add(of(BOTTOM_LEFT, BOTTOM_CENTRE, BOTTOM_RIGHT));

      // vertical
      LINES.add(of(TOP_LEFT, MIDDLE_LEFT, BOTTOM_LEFT));
      LINES.add(of(TOP_CENTRE, CENTRE, BOTTOM_CENTRE));
      LINES.add(of(TOP_RIGHT, MIDDLE_RIGHT, BOTTOM_RIGHT));

      // diagonal
      LINES.add(of(TOP_LEFT, CENTRE, BOTTOM_RIGHT));
      LINES.add(of(TOP_RIGHT, CENTRE, BOTTOM_LEFT));
   }
   private static final EnumSet<Move> ALL = allOf(Move.class);
   private static final EnumSet<Move> NONE = noneOf(Move.class);
   private static final EnumSet<Move> CORNERS = of(TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT);
   private static final EnumSet<Move> SIDES = of(TOP_CENTRE, MIDDLE_RIGHT, BOTTOM_CENTRE, BOTTOM_LEFT);

   private final EnumSet<Move> oState;
   private final EnumSet<Move> xState;

   Board() {
      this(NONE, NONE);
   }

   private Board(EnumSet<Move> oState, EnumSet<Move> xState) {
      this.oState = oState;
      this.xState = xState;
   }

   Board update(Symbol symbol, Move move) {
      if (isInvalid(move)) {
         throw new IllegalArgumentException("Invalid move:" + move + " o: " + oState + " x: " + xState);
      } else if (symbol == X) {
         return new Board(oState, copyAndAdd(xState, move));
      } else {
         return new Board(copyAndAdd(oState, move), xState);
      }
   }

   private EnumSet<Move> copyAndAdd(EnumSet<Move> original, Move move) {
      EnumSet<Move> updatedMoves = copyOf(original);
      updatedMoves.add(move);
      return updatedMoves;
   }

   private boolean isInvalid(Move move) {
      return !isFree(move) || isWinner(xState) || isWinner(oState) || isDraw();
   }

   boolean isDraw() {
      return oState.size() + xState.size() == MAX_MOVES;
   }

   boolean isWinner(Symbol symbol) {
      return isWinner(getState(symbol));
   }

   boolean isWinner(EnumSet<Move> moves) {
      return LINES.stream().filter(l -> getIntersectionSize(l, moves) == 3).findFirst().isPresent();
   }

   Move getWinningMove(Symbol symbol) {
      EnumSet<Move> moves = getState(symbol);
      EnumSet<Move> opponents = getState(symbol.getOpponent());
      Optional<EnumSet<Move>> o = LINES.stream().filter(l -> getIntersectionSize(l, moves) == 2 && getIntersectionSize(l, opponents) == 0).findFirst();
      return o.map(l -> first(difference(l, moves))).orElse(null);
   }

   Move getFreeCorner() {
      return getFree(CORNERS);
   }

   Move getFreeSide() {
      return getFree(SIDES);
   }

   Move getFreeCentre() {
      return isFree(CENTRE) ? CENTRE : null;
   }

   Move getFreeMove() {
      return getFree(ALL);
   }

   boolean isOccupied(Move m, Symbol s) {
      return getState(s).contains(m);
   }

   boolean isFree(Move m) {
      return !oState.contains(m) && !xState.contains(m);
   }

   private Move getFree(EnumSet<Move> possibles) {
      return possibles.stream().filter(this::isFree).findFirst().orElse(null);
   }

   private int getIntersectionSize(EnumSet<Move> a, EnumSet<Move> b) {
      return intersection(a, b).size();
   }

   private EnumSet<Move> intersection(EnumSet<Move> a, EnumSet<Move> b) {
      EnumSet<Move> intersection = copyOf(a);
      intersection.retainAll(b);
      return intersection;
   }

   private EnumSet<Move> difference(EnumSet<Move> a, EnumSet<Move> b) {
      EnumSet<Move> intersection = copyOf(a);
      intersection.removeAll(b);
      return intersection;
   }

   private Move first(EnumSet<Move> s) {
      return s.iterator().next();
   }

   private EnumSet<Move> getState(Symbol s) {
      return s == X ? xState : oState;
   }
}
