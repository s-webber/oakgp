package org.oakgp.examples.snake;

import java.util.ArrayDeque;
import java.util.Deque;

import org.oakgp.util.JavaUtilRandomAdapter;
import org.oakgp.util.Random;

// http://gpbib.cs.ucl.ac.uk/gp-html/article1175.html
// https://www.gamedev.net/articles/programming/artificial-intelligence/application-of-genetic-programming-to-the-snake-r1175/
public final class Snake {
   private static final Random RANDOM = new JavaUtilRandomAdapter();
   private static final int WIDTH = 20;
   private static final int HEIGHT = 11;
   private static final Cell NULL_CELL = new Cell(-1, -1);
   private static final Cell[] CELLS = new Cell[HEIGHT * WIDTH];
   private static final Cell[][] GRID = new Cell[HEIGHT][WIDTH];
   static {
      int id = 0;
      for (int v = 0; v < HEIGHT; v++) {
         for (int h = 0; h < WIDTH; h++) {
            Cell cell = new Cell(h, v);
            CELLS[id++] = cell;
            GRID[v][h] = cell;
         }
      }

      for (int v = 0; v < HEIGHT; v++) {
         for (int h = 0; h < WIDTH; h++) {
            Cell cell = GRID[v][h];
            if (v > 0) {
               cell.north = GRID[v - 1][h];
            }
            if (h < WIDTH - 1) {
               cell.east = GRID[v][h + 1];
            }
            if (h > 0) {
               cell.west = GRID[v][h - 1];
            }
            if (v < HEIGHT - 1) {
               cell.south = GRID[v + 1][h];
            }
         }
      }
   }

   private final boolean draw;
   private Cell apple;
   private Cell head;
   private final Deque<Cell> tail;
   private Direction previousMove;
   private boolean isDead;
   private int movesWithoutApple;
   private int moveCtr;

   Snake(boolean draw) {
      this.draw = draw;
      apple = GRID[5][10];
      head = GRID[10][8];
      tail = new ArrayDeque<>();
      Cell temp = head;
      for (int i = 0; i < 8; i++) {
         temp = temp.move(Direction.WEST);
         tail.addLast(temp);
      }
      previousMove = Direction.EAST;
   }

   public void forward() {
      move(previousMove);
   }

   public void left() {
      move(previousMove.turnLeft());
   }

   public void right() {
      move(previousMove.turnRight());
   }

   public boolean isFoodAhead() {
      switch (previousMove) {
         case NORTH:
            return head.x == apple.x && head.y > apple.y;
         case EAST:
            return head.y == apple.y && head.x < apple.x;
         case SOUTH:
            return head.x == apple.x && head.y < apple.y;
         case WEST:
            return head.y == apple.y && head.x > apple.x;
         default:
            throw new IllegalStateException();
      }
   }

   public boolean isDangerAhead() {
      return isDanger(head.move(previousMove));
   }

   public boolean isDangerRight() {
      return isDanger(head.move(previousMove.turnRight()));
   }

   public boolean isDangerLeft() {
      return isDanger(head.move(previousMove.turnLeft()));
   }

   public boolean isDangerTwoAhead() {
      Cell oneAhead = head.move(previousMove);
      return isDanger(oneAhead) || isDanger(oneAhead.move(previousMove));
   }

   public boolean isFoodNorth() {
      return head.y > apple.y;
   }

   public boolean isFoodEast() {
      return head.x < apple.x;
   }

   public boolean isMovingNorth() {
      return previousMove == Direction.NORTH;
   }

   public boolean isMovingSouth() {
      return previousMove == Direction.SOUTH;
   }

   public boolean isMovingEast() {
      return previousMove == Direction.WEST;
   }

   public boolean isMovingWest() {
      return previousMove == Direction.EAST;
   }

   public Direction getDirection() {
      return previousMove;
   }

   private void move(Direction nextMove) {
      if (gameOver()) {
         return;
      }

      tail.addFirst(head);
      head = head.move(nextMove);
      previousMove = nextMove;
      movesWithoutApple++;
      moveCtr++;

      if (isDanger(head)) {
         isDead = true;
         head = NULL_CELL;
      } else if (head == apple) {
         apple = findEmptyCell(head, tail);
         movesWithoutApple = 0;
      } else {
         tail.pollLast();
      }

      if (draw) {
         draw(head, tail, apple);
      }
   }

   private boolean isDanger(Cell c) {
      return c == null || tail.contains(c);
   }

   private Cell findEmptyCell(Cell head, Deque<Cell> tail) {
      int emptyCellsCount = CELLS.length - tail.size() - 1;
      if (emptyCellsCount == 0) {
         return NULL_CELL;
      }

      int idx = RANDOM.nextInt(emptyCellsCount);
      for (int i = 0; i < CELLS.length; i++) {
         Cell cell = CELLS[idx];
         if (head == cell || tail.contains(cell)) {
            idx++;
         } else if (idx == i) {
            return cell;
         }
      }
      throw new IllegalStateException(tail.size() + " ");
   }

   private void draw(Cell head, Deque<Cell> tail, Cell apple) {
      for (int v = 0; v < HEIGHT; v++) {
         for (int h = 0; h < WIDTH; h++) {
            Cell cell = GRID[v][h];
            if (cell == head) {
               System.out.print('H');
            } else if (cell == apple) {
               System.out.print('A');
            } else if (tail.contains(cell)) {
               System.out.print('T');
            } else {
               System.out.print('-');
            }
         }
         System.out.println();
      }
      System.out.println();
   }

   public boolean gameOver() {
      return isDead || movesWithoutApple > (WIDTH * HEIGHT) * 5;
   }

   public int getFitness() {
      if (tail.size() > 99) {
         System.out.println("tail size " + tail.size());
      }
      return CELLS.length - tail.size() - 1;
   }

   public int getMoves() {
      return moveCtr;
   }
}
