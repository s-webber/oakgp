package org.oakgp.examples.snake;

import static org.oakgp.examples.snake.Direction.EAST;
import static org.oakgp.examples.snake.Direction.NORTH;
import static org.oakgp.examples.snake.Direction.SOUTH;
import static org.oakgp.examples.snake.Direction.WEST;

import java.util.ArrayDeque;
import java.util.Deque;

import org.oakgp.Assignments;
import org.oakgp.node.Node;
import org.oakgp.rank.fitness.FitnessFunction;
import org.oakgp.util.JavaUtilRandomAdapter;
import org.oakgp.util.Random;

final class Snake implements FitnessFunction {
   private final Random random = new JavaUtilRandomAdapter();
   private final int width;
   private final int height;
   private final Cell[] cells;
   private final Cell[][] grid;

   Snake(int width) {
      this.width = width;
      this.height = width;
      this.cells = new Cell[height * width];
      this.grid = new Cell[height][width];

      for (int v = 0; v < height; v++) {
         for (int h = 0; h < width; h++) {
            int id = (v * height) + h;
            Cell cell = new Cell(h, v);
            cells[id] = cell;
            grid[v][h] = cell;
         }
      }

      for (int v = 0; v < height; v++) {
         for (int h = 0; h < width; h++) {
            Cell cell = grid[v][h];
            if (v > 0) {
               cell.north = grid[v - 1][h];
            }
            if (h < width - 1) {
               cell.east = grid[v][h + 1];
            }
            if (h > 0) {
               cell.west = grid[v][h - 1];
            }
            if (v < height - 1) {
               cell.south = grid[v + 1][h];
            }
         }
      }
   }

   @Override
   public double evaluate(Node n) {
      double score = 0;
      for (int i = 0; i < 5; i++) {
         score += evaluate(n, false);
      }
      return score;
   }

   public double evaluate(Node n, boolean draw) {
      Cell head = grid[2][width / 2];
      Deque<Cell> tail = new ArrayDeque<>();
      Direction previousMove = NORTH;
      Cell apple = grid[0][width / 2];
      int movesWithoutApple = 0;

      while (movesWithoutApple < cells.length * 2) {
         movesWithoutApple++;

         Assignments assignments = createAssignments(head, tail, previousMove);
         Direction nextMove = n.evaluate(assignments);
         if (draw) {
            draw(head, tail, apple);
            System.out.println(assignments + " -> " + nextMove);
         }

         if (nextMove == previousMove.opposite() && !tail.isEmpty()) {
            // hit self
            break;
         }

         tail.addFirst(head);
         head = head.move(nextMove);
         previousMove = nextMove;

         if (head == null) {
            // hit wall
            break;
         } else if (tail.contains(head)) {
            // hit self
            break;
         } else if (head == apple) {
            apple = findEmptyCell(head, tail);
            movesWithoutApple = 0;
         } else {
            tail.pollLast();
         }
      }

      return cells.length - tail.size();
   }

   private Cell findEmptyCell(Cell head, Deque<Cell> tail) {
      int emptyCellsCount = cells.length - tail.size() - 1;
      if (emptyCellsCount == 0) {
         return null;
      }

      int idx = random.nextInt(emptyCellsCount);
      for (int i = 0; i < cells.length; i++) {
         Cell cell = cells[idx];
         if (head == cell || tail.contains(cell)) {
            idx++;
         } else if (idx == i) {
            return cell;
         }
      }
      throw new IllegalStateException(tail.size() + " ");
   }

   private Assignments createAssignments(Cell head, Deque<Cell> tail, Direction previousMove) {
      int[] distances = new int[] {distance(head, tail, NORTH), distance(head, tail, EAST), distance(head, tail, SOUTH), distance(head, tail, WEST)};
      return Assignments.createAssignments(distances, previousMove);
   }

   private int distance(Cell head, Deque<Cell> tail, Direction d) {
      int distance = 0;

      Cell next = head.move(d);
      while (next != null && !tail.contains(next)) {
         next = next.move(d);
         distance++;
      }

      return distance;
   }

   private void draw(Cell head, Deque<Cell> tail, Cell apple) {
      for (int v = 0; v < height; v++) {
         for (int h = 0; h < width; h++) {
            Cell cell = grid[v][h];
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
      try {
         Thread.sleep(100);
      } catch (Exception e) {
      }
   }
}
