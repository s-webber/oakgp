/*
 * Copyright 2015 S. Webber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oakgp.serialize;

import static org.oakgp.Arguments.createArguments;
import static org.oakgp.Type.arrayType;
import static org.oakgp.Type.bigDecimalType;
import static org.oakgp.Type.integerType;
import static org.oakgp.Type.longType;
import static org.oakgp.Type.stringType;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.oakgp.FunctionSet;
import org.oakgp.Type;
import org.oakgp.VariableSet;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.util.Utils;

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

   private final CharReader cr;
   private final FunctionSet functionSet;
   private final VariableSet variableSet;

   public NodeReader(String input, FunctionSet functionSet, VariableSet variableSet) {
      StringReader sr = new StringReader(input);
      this.cr = new CharReader(new BufferedReader(sr));
      this.functionSet = functionSet;
      this.variableSet = variableSet;
   }

   public Node readNode() throws IOException {
      return nextNode(nextToken());
   }

   private Node nextNode(String firstToken) throws IOException {
      if (firstToken == FUNCTION_START_STRING) {
         return nextFunctionNode();
      } else if (firstToken == STRING_STRING) {
         return nextConstantNode();
      } else if (firstToken == ARRAY_START_STRING) {
         return nextArrayConstantNode();
      } else if (firstToken.charAt(0) == 'v') {
         return nextVariable(firstToken);
      } else {
         return nextLiteral(firstToken);
      }
   }

   private Node nextFunctionNode() throws IOException {
      String functionName = nextToken();
      List<Node> arguments = new ArrayList<>();
      List<Type> types = new ArrayList<>();
      String nextToken;
      while ((nextToken = nextToken()) != FUNCTION_END_STRING) {
         Node n = nextNode(nextToken);
         arguments.add(n);
         types.add(n.getType());
      }
      Function function = functionSet.getFunction(functionName, types);
      return new FunctionNode(function, createArguments(arguments));
   }

   private Node nextConstantNode() throws IOException {
      StringBuilder sb = new StringBuilder();
      int next;
      while ((next = cr.next()) != STRING_CHAR) {
         assertNotEndOfStream(next);
         sb.append((char) next);
      }
      return new ConstantNode(sb.toString(), stringType());
   }

   private Node nextArrayConstantNode() throws IOException {
      List<Node> arguments = new ArrayList<>();
      String nextToken;
      Type t = null;
      while ((nextToken = nextToken()) != ARRAY_END_STRING) {
         Node n = nextNode(nextToken);
         if (t == null) {
            t = n.getType();
         } else if (t != n.getType()) {
            throw new IllegalStateException("Mixed type array elements: " + t + " and " + n.getType());
         }
         arguments.add(n);
      }
      return new ConstantNode(createArguments(arguments), arrayType(t));
   }

   private Node nextVariable(String firstToken) {
      int id = Integer.parseInt(firstToken.substring(1));
      return variableSet.getById(id);
   }

   private ConstantNode nextLiteral(String token) {
      switch (token) {
         case "true":
            return Utils.TRUE_NODE;
         case "false":
            return Utils.FALSE_NODE;
         default:
            if (isNumber(token)) {
               char suffix = token.charAt(token.length() - 1);
               switch (suffix) {
                  case 'D':
                     return new ConstantNode(toBigDecimal(token.substring(0, token.length() - 1)), bigDecimalType());
                  case 'L':
                     return new ConstantNode(Long.valueOf(token.substring(0, token.length() - 1)), longType());
                  default:
                     return new ConstantNode(Integer.parseInt(token), integerType());
               }
            } else {
               Function function = functionSet.getFunction(token);
               return new ConstantNode(function, getFunctionType(function));
            }
      }
   }

   private BigDecimal toBigDecimal(String number) {
      switch (number) {
         case "0":
            return BigDecimal.ZERO;
         case "1":
            return BigDecimal.ONE;
         case "10":
            return BigDecimal.TEN;
         default:
            return new BigDecimal(number);
      }
   }

   private Type getFunctionType(Function function) {
      Signature signature = function.getSignature();
      Type[] types = new Type[signature.getArgumentTypesLength() + 1];
      types[0] = signature.getReturnType();
      for (int i = 1; i < types.length; i++) {
         types[i] = signature.getArgumentType(i - 1);
      }
      return Type.functionType(types);
   }

   private static boolean isNumber(String token) {
      char firstChar = token.charAt(0);
      if (firstChar == '-') {
         return token.length() > 1 && isNumber(token.charAt(1));
      } else {
         return isNumber(firstChar);
      }
   }

   private static boolean isNumber(char c) {
      return c >= '0' && c <= '9';
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
         } while ((c = cr.next()) != -1 && isFunctionIdentifierPart(c));
         cr.rewind(c);
         return sb.toString();
      }
   }

   public static boolean isValidDisplayName(String displayName) {
      if (displayName == null || displayName.length() == 0 || isNumber(displayName)) {
         return false;
      }

      for (char c : displayName.toCharArray()) {
         if (!isFunctionIdentifierPart(c)) {
            return false;
         }
      }

      return true;
   }

   private static boolean isFunctionIdentifierPart(int c) {
      return c != FUNCTION_END_CHAR && c != FUNCTION_START_CHAR && c != ARRAY_START_CHAR && c != ARRAY_END_CHAR && c != STRING_CHAR
            && !Character.isWhitespace(c);
   }

   private static void assertNotEndOfStream(int c) {
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

      CharReader(BufferedReader br) {
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

      void rewind(int c) {
         previous = c;
      }

      @Override
      public void close() throws IOException {
         br.close();
      }
   }
}
