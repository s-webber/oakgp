package org.oakgp.util;

import static org.oakgp.Type.integerType;

import java.io.IOException;
import java.util.Scanner;

import org.oakgp.Assignments;
import org.oakgp.NodeSimplifier;
import org.oakgp.node.Node;
import org.oakgp.serialize.NodeReader;
import org.oakgp.serialize.NodeWriter;

public class Repl {
   public static void main(String[] args) throws IOException {
      Scanner scanner = new Scanner(System.in);
      Assignments assignments = Assignments.createAssignments(2, 3);
      while (true) {
         System.out.print("> ");
         String input = scanner.nextLine();
         Node expression = new NodeReader(input, integerType(), integerType()).readNode();
         Node simplifiedResult = new NodeSimplifier().simplify(expression);
         System.out.println(new NodeWriter().writeNode(expression));
         System.out.println(new NodeWriter().writeNode(simplifiedResult));
         System.out.println(expression.evaluate(assignments).toString());
         System.out.println(simplifiedResult.evaluate(assignments).toString());
      }
   }
}
