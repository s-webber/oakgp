package org.oakgp.util;

import java.io.IOException;
import java.util.Scanner;

import org.oakgp.node.Node;
import org.oakgp.serialize.NodeReader;

public class Repl {
	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.print("> ");
			String input = scanner.nextLine();
			Node expression = new NodeReader(input).readNode();
			Object result = expression.evaluate(null);
			System.out.println(result);
		}
	}
}
