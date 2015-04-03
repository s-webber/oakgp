package org.oakgp.serialize;

import static org.oakgp.Type.ARRAY;
import static org.oakgp.Type.BOOLEAN;
import static org.oakgp.Type.INTEGER;
import static org.oakgp.Type.OPERATOR;
import static org.oakgp.Type.STRING;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.oakgp.Arguments;
import org.oakgp.Type;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;
import org.oakgp.function.Operator;

/**
 * Creates {@code Node} instances from {@code String} representations.
 * <p>
 * e.g. The {@code String}:
 *
 * <pre>
 * (+ 9 5)
 * </pre>
 *
 * will produce the {@code Node}:
 *
 * <pre>
 * new FunctionNode(new Add(), createArguments(new ConstantNode(9), new ConstantNode(5))
 * </pre>
 */
public final class NodeReader implements Closeable {
   private static final ConstantNode TRUE_NODE = new ConstantNode(Boolean.TRUE, BOOLEAN); // TODO move somewhere it can be shared
   private static final ConstantNode FALSE_NODE = new ConstantNode(Boolean.FALSE, BOOLEAN); // TODO move somewhere it can be shared
   private static final char FUNCTION_START_CHAR = '(';
   private static final String FUNCTION_START_STRING = Character.toString(FUNCTION_START_CHAR);
   private static final char FUNCTION_END_CHAR = ')';
   private static final String FUNCTION_END_STRING = Character.toString(FUNCTION_END_CHAR);
   private static final char STRING_CHAR = '\"';
   private static final String STRING_STRING = Character.toString(STRING_CHAR);
   private static final char ARRAY_START_CHAR = '[';
   private static final String ARRAY_START_STRING = Character.toString(ARRAY_START_CHAR);
   private static final char ARRAY_END_CHAR = ']';
   private static final String ARRAY_END_STRING = Character.toString(ARRAY_END_CHAR);

   private final SymbolMap symbolMap = new SymbolMap();
   private final CharReader cr;
   private final Type[] variableTypes;

   public NodeReader(String input, Type... variableTypes) {
      StringReader sr = new StringReader(input);
      this.cr = new CharReader(new BufferedReader(sr));
      this.variableTypes = variableTypes;
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
         return new FunctionNode(operator, createArgumentsFromList(arguments));
      } else if (firstToken == STRING_STRING) {
         StringBuilder sb = new StringBuilder();
         int next;
         while ((next = cr.next()) != STRING_CHAR) {
            assertNotEndOfStream(next);
            sb.append((char) next);
         }
         return new ConstantNode(sb.toString(), STRING);
      } else if (firstToken == ARRAY_START_STRING) {
         List<Node> arguments = new ArrayList<>();
         String nextToken;
         while ((nextToken = nextToken()) != ARRAY_END_STRING) {
            arguments.add(nextNode(nextToken));
         }
         return new ConstantNode(createArgumentsFromList(arguments), ARRAY);
      } else if (firstToken.charAt(0) == 'v') {
         int id = Integer.parseInt(firstToken.substring(1));
         return new VariableNode(id, variableTypes[id]);
      } else {
         return parseLiteral(firstToken);
      }
   }

   // TODO move to Arguments
   private Arguments createArgumentsFromList(List<Node> arguments) {
      return Arguments.createArguments(arguments.toArray(new Node[arguments.size()]));
   }

   private ConstantNode parseLiteral(String token) {
      switch (token) {
      case "true":
         return TRUE_NODE;
      case "false":
         return FALSE_NODE;
      default:
         if (isNumber(token)) {
            return new ConstantNode(Integer.parseInt(token), INTEGER);
         } else {
            return new ConstantNode(symbolMap.getOperator(token), OPERATOR);
         }
      }
   }

   private boolean isNumber(String token) {
      int c = token.charAt(0);
      return c == '-' || (c >= '0' && c <= '9');
   }

   private String nextToken() throws IOException {
      cr.skipWhitespace();
      int c = cr.next();
      if (c == FUNCTION_START_CHAR) {
         return FUNCTION_START_STRING;
      } else if (c == FUNCTION_END_CHAR) {
         return FUNCTION_END_STRING;
      } else if (c == STRING_CHAR) {
         return STRING_STRING;
      } else if (c == ARRAY_START_CHAR) {
         return ARRAY_START_STRING;
      } else if (c == ARRAY_END_CHAR) {
         return ARRAY_END_STRING;
      } else {
         assertNotEndOfStream(c);
         StringBuilder sb = new StringBuilder();
         do {
            sb.append((char) c);
         } while ((c = cr.next()) != -1 && c != FUNCTION_END_CHAR && c != FUNCTION_START_CHAR && c != ARRAY_START_CHAR && c != ARRAY_END_CHAR
               && c != STRING_CHAR && !Character.isWhitespace(c));
         cr.rewind(c);
         return sb.toString();
      }
   }

   private void assertNotEndOfStream(int c) {
      if (c == -1) {
         throw new IllegalStateException();
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
