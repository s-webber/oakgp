package org.oakgp.serialize;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.oakgp.Arguments;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;
import org.oakgp.operator.Operator;

public final class NodeReader implements Closeable {
	private static final char FUNCTION_START_CHAR = '(';
	private static final String FUNCTION_START_STRING = Character.toString(FUNCTION_START_CHAR);
	private static final char FUNCTION_END_CHAR = ')';
	private static final String FUNCTION_END_STRING = Character.toString(FUNCTION_END_CHAR);

	private final SymbolMap symbolMap = new SymbolMap();
	private final CharReader cr;

	public NodeReader(String input) {
		StringReader sr = new StringReader(input);
		cr = new CharReader(new BufferedReader(sr));
	}

	public Node readNode() throws IOException {
		return nextNode(nextToken());
	}

	private Node nextNode(String firstToken) throws IOException {
		if (firstToken == FUNCTION_START_STRING) {
			String functionName = nextToken();
			Operator operator = symbolMap.getOperator(functionName);
			List<Node> arguments = new ArrayList<>();
			String nextToken;
			while ((nextToken = nextToken()) != FUNCTION_END_STRING) {
				arguments.add(nextNode(nextToken));
			}
			return new FunctionNode(operator, Arguments.createArguments(arguments.toArray(new Node[arguments.size()])));
		} else if (firstToken.charAt(0) == 'v') {
			return new VariableNode(Integer.parseInt(firstToken.substring(1)));
		} else {
			return new ConstantNode(parseLiteral(firstToken));
		}
	}

	private Object parseLiteral(String firstToken) {
		switch (firstToken) {
		case "true":
			return Boolean.TRUE;
		case "false":
			return Boolean.FALSE;
		default:
			return Integer.parseInt(firstToken);
		}
	}

	private String nextToken() throws IOException {
		cr.skipWhitespace();
		int c = cr.next();
		if (c == FUNCTION_START_CHAR) {
			return FUNCTION_START_STRING;
		} else if (c == FUNCTION_END_CHAR) {
			return FUNCTION_END_STRING;
		} else if (c == -1) {
			throw new IllegalStateException();
		} else {
			StringBuilder sb = new StringBuilder();
			do {
				sb.append((char) c);
			} while ((c = cr.next()) != -1 && c != FUNCTION_START_CHAR && c != FUNCTION_END_CHAR && !Character.isWhitespace(c));
			cr.rewind(c);
			return sb.toString();
		}
	}

	@Override
	public void close() throws IOException {
		cr.close();
	}

	public boolean isEndOfStream() throws IOException {
		return cr.isEndOfStream();
	}

	private static final class CharReader implements Closeable {
		private final BufferedReader br;
		private int previous = -1;

		CharReader(BufferedReader br) { // TODO create br in constructor
			this.br = br;
		}

		boolean isEndOfStream() throws IOException {
			skipWhitespace();
			return previous == -1 && (previous = br.read()) == -1;
		}

		void skipWhitespace() throws IOException {
			int next = previous == -1 ? br.read() : previous;
			while (next != -1 && Character.isWhitespace(next)) {
				next = br.read();
			}
			rewind(next);
		}

		int next() throws IOException {
			if (previous != -1) {
				int result = previous;
				previous = -1;
				return result;
			} else {
				return br.read();
			}
		}

		void rewind(int c) { // TODO don't accept argument (already know what to rewind to)
			previous = c;
		}

		@Override
		public void close() throws IOException {
			br.close();
		}
	}
}