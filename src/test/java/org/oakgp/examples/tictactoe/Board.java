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
import static org.oakgp.examples.tictactoe.Symbol.O;
import static org.oakgp.examples.tictactoe.Symbol.X;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

   private final Map<Symbol, EnumSet<Move>> state;

   Board() {
      this.state = new HashMap<>();
      state.put(O, NONE);
      state.put(X, NONE);
   }

   private Board(Map<Symbol, EnumSet<Move>> state) {
      this.state = state;
   }

   Board update(Symbol symbol, Move move) {
      if (isInvalid(move)) {
         throw new IllegalArgumentException("Invalid move:" + move + " state: " + state);
      }

      Map<Symbol, EnumSet<Move>> copy = new HashMap<>();
      copy.put(symbol.getOpponent(), state.get(symbol.getOpponent()));
      EnumSet<Move> updatedMoves = copyOf(state.get(symbol));
      updatedMoves.add(move);
      copy.put(symbol, updatedMoves);
      return new Board(copy);
   }

   private boolean isInvalid(Move move) {
      return !isFree(move) || isWinner(X) || isWinner(O) || isDraw();
   }

   boolean isDraw() {
      return state.get(Symbol.O).size() + state.get(Symbol.X).size() == MAX_MOVES;
   }

   boolean isWinner(Symbol symbol) {
      EnumSet<Move> moves = state.get(symbol);
      return LINES.stream().filter(l -> getIntersectionSize(l, moves) == 3).findFirst().isPresent();
   }

   Move getWinningMove(Symbol symbol) {
      EnumSet<Move> moves = state.get(symbol);
      EnumSet<Move> opponents = state.get(symbol.getOpponent());
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

   private boolean isFree(Move m) {
      return !state.get(O).contains(m) && !state.get(X).contains(m);
   }

   private Move first(EnumSet<Move> s) {
      return s.iterator().next();
   }
}
