package org.oakgp.examples.gridwar;

import static org.oakgp.examples.gridwar.GridWar.GRID_WIDTH;

import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;

import org.oakgp.Assignments;
import org.oakgp.Type;
import org.oakgp.node.Node;

/** Represents a human player in a GridWar game. */
class Human implements Node {
   @Override
   public Integer evaluate(Assignments assignments) {
      int playerX = (int) assignments.get(0);
      int playerY = (int) assignments.get(1);
      int opponentX = (int) assignments.get(3);
      int opponentY = (int) assignments.get(4);
      for (int y = 0; y < GRID_WIDTH; y++) {
         for (int x = 0; x < GRID_WIDTH; x++) {
            char c;
            if (x == playerX && y == playerY) {
               c = 'X';
            } else if (x == opponentX && y == opponentY) {
               c = 'O';
            } else {
               c = '-';
            }
            System.out.print(c);
         }
         System.out.println();
      }
      System.out.println(" 0");
      System.out.println("3 1");
      System.out.println(" 2");
      System.out.println("your move");
      return readInt();
   }

   @SuppressWarnings("resource")
   private int readInt() {
      return new Scanner(System.in).nextInt();
   }

   @Override
   public int getNodeCount() {
      return 0;
   }

   @Override
   public int getNodeCount(Predicate<Node> treeWalkerStrategy) {
      return 0;
   }

   @Override
   public Node replaceAt(int idx, Function<Node, Node> replacement) {
      return null;
   }

   @Override
   public Node getAt(int idx) {
      return null;
   }

   @Override
   public int getDepth() {
      return 0;
   }

   @Override
   public Type getType() {
      return null;
   }
}
