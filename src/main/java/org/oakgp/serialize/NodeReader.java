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

import static java.util.Collections.unmodifiableList;
import static org.oakgp.Type.bigDecimalType;
import static org.oakgp.Type.bigIntegerType;
import static org.oakgp.Type.doubleType;
import static org.oakgp.Type.integerType;
import static org.oakgp.Type.listType;
import static org.oakgp.Type.longType;
import static org.oakgp.Type.mapType;
import static org.oakgp.Type.stringType;
import static org.oakgp.node.ChildNodes.createChildNodes;
import static org.oakgp.util.Utils.FALSE_NODE;
import static org.oakgp.util.Utils.TRUE_NODE;
import static org.oakgp.util.Utils.copyOf;
import static org.oakgp.util.Void.VOID_CONSTANT;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;
import org.oakgp.primitive.VariableSet;

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
   private static final String FUNCTION_START_STRING = "" + FUNCTION_START_CHAR;
   private static final char FUNCTION_END_CHAR = ')';
   private static final String FUNCTION_END_STRING = "" + FUNCTION_END_CHAR;
   private static final char STRING_CHAR = '\"';
   private static final String STRING_STRING = "" + STRING_CHAR;
   private static final char LIST_START_CHAR = '[';
   private static final String LIST_START_STRING = "" + LIST_START_CHAR;
   private static final char LIST_END_CHAR = ']';
   private static final String LIST_END_STRING = "" + LIST_END_CHAR;
   private static final char MAP_START_CHAR = '{';
   private static final String MAP_START_STRING = "" + MAP_START_CHAR;
   private static final char MAP_END_CHAR = '}';
   private static final String MAP_END_STRING = "" + MAP_END_CHAR;

   private final Map<Predicate<String>, java.util.function.Function<String, Node>> readers;
   private final CharReader cr;
   private final Function[] functions;
   private final ConstantNode[] constants;
   private final VariableSet variableSet;

   /**
    * Creates a {@code NodeReader} that reads from the given {@code java.lang.String}.
    *
    * @param input
    *           contains the s-expressions, representing programs as tree structures, that will be read to construct new {@code Node} instances
    * @param functions
    *           a collection of {@code Function}s to use to represent functions read from {@code input}
    * @param constants
    *           a collection of {@code ConstantNode}s to use to represent constants read from {@code input}
    * @param variableSet
    *           the variable set to use to represent variables read from {@code input}
    */
   public NodeReader(String input, Function[] functions, ConstantNode[] constants, VariableSet variableSet) {
      StringReader sr = new StringReader(input);
      this.cr = new CharReader(new BufferedReader(sr));
      this.functions = copyOf(functions);
      this.constants = copyOf(constants);
      this.variableSet = variableSet;
      this.readers = createReaders();
   }

   private Map<Predicate<String>, java.util.function.Function<String, Node>> createReaders() {
      Map<Predicate<String>, java.util.function.Function<String, Node>> m = new LinkedHashMap<>();
      m.put(NodeReader::isVariable, this::getVariable);
      m.put(s -> "true".equals(s), s -> TRUE_NODE);
      m.put(s -> "false".equals(s), s -> FALSE_NODE);
      m.put(s -> "void".equals(s), s -> VOID_CONSTANT);
      m.put(NodeReader::isLong, this::createLongConstant);
      m.put(NodeReader::isBigInteger, this::createBigIntegerConstant);
      m.put(NodeReader::isBigDecimal, this::createBigDecimalConstant);
      m.put(NodeReader::isDecimalNumber, this::createDoubleConstant);
      m.put(NodeReader::isNumber, this::createIntegerConstant);
      m.put(this::isConstant, this::getConstant);
      m.put(s -> true, this::createFunctionConstant);
      return m;
   }

   /** Creates and returns a {@code Node} which represents the next s-expression read from the input. */
   public Node readNode() throws IOException {
      return nextNode(nextToken());
   }

   private String nextToken() throws IOException {
      cr.skipWhitespace();
      int c = cr.next();
      switch (c) {
         case FUNCTION_START_CHAR:
            return FUNCTION_START_STRING;
         case FUNCTION_END_CHAR:
            return FUNCTION_END_STRING;
         case STRING_CHAR:
            return STRING_STRING;
         case LIST_START_CHAR:
            return LIST_START_STRING;
         case LIST_END_CHAR:
            return LIST_END_STRING;
         case MAP_START_CHAR:
            return MAP_START_STRING;
         case MAP_END_CHAR:
            return MAP_END_STRING;
         default:
            assertNotEndOfStream(c);
            StringBuilder sb = new StringBuilder();
            do {
               sb.append((char) c);
            } while ((c = cr.next()) != -1 && isFunctionIdentifierPart(c));
            cr.rewind(c);
            return sb.toString();
      }
   }

   private Node nextNode(String firstToken) throws IOException {
      switch (firstToken) {
         case FUNCTION_START_STRING:
            return createFunctionNode();
         case STRING_STRING:
            return createStringConstantNode();
         case LIST_START_STRING:
            return createListConstantNode();
         case MAP_START_STRING:
            return createMapConstantNode();
         default:
            return createNode(firstToken);
      }
   }

   private FunctionNode createFunctionNode() throws IOException {
      String functionName = nextToken();
      List<Node> arguments = new ArrayList<>();
      List<Type> types = new ArrayList<>();
      String nextToken;
      while (notFunctionEnd(nextToken = nextToken())) {
         Node n = nextNode(nextToken);
         arguments.add(n);
         types.add(n.getType());
      }
      Function function = getFunction(functionName, types);
      return new FunctionNode(function, createChildNodes(arguments));
   }

   private boolean notFunctionEnd(String token) {
      return !FUNCTION_END_STRING.equals(token);
   }

   private Function getFunction(String functionName, List<Type> types) {
      for (Function f : functions) {
         if (isMatch(functionName, types, f)) {
            return f;
         }
      }

      StringBuilder sb = new StringBuilder();
      for (Function f : functions) {
         sb.append(" [name: " + f.getDisplayName() + " arguments: " + f.getSignature().getArgumentTypes() + "]");
      }
      throw new IllegalArgumentException("Could not find version of function: " + functionName + " for: " + types + " in:" + sb);
   }

   private boolean isMatch(String functionName, List<Type> types, Function f) {
      return functionName.equals(f.getDisplayName()) && types.equals(f.getSignature().getArgumentTypes());
   }

   private ConstantNode createStringConstantNode() throws IOException {
      StringBuilder sb = new StringBuilder();
      int next;
      while ((next = cr.next()) != STRING_CHAR) {
         assertNotEndOfStream(next);
         sb.append((char) next);
      }
      return new ConstantNode(sb.toString(), stringType());
   }

   private ConstantNode createListConstantNode() throws IOException {
      List<Object> arguments = new ArrayList<>();
      String nextToken;
      Type t = null;
      while (notListEnd(nextToken = nextToken())) {
         Node n = nextNode(nextToken);
         if (t == null) {
            t = n.getType();
         } else if (t != n.getType()) {
            throw new IllegalStateException("Mixed type list elements: " + t + " and " + n.getType());
         }
         arguments.add(n.evaluate(null));
      }
      // TODO what is the correct behaviour when the list is empty?
      return new ConstantNode(unmodifiableList(arguments), listType(t));
   }

   private boolean notListEnd(String token) {
      return !LIST_END_STRING.equals(token);
   }

   private ConstantNode createMapConstantNode() throws IOException {
      Map<Object, Object> map = new LinkedHashMap<>();
      String nextToken;
      Type keyType = null;
      Type valueType = null;
      while (notMapEnd(nextToken = nextToken())) {
         Node key = nextNode(nextToken);

         String valueToken = nextToken();
         if (isMapEnd(valueToken)) {
            throw new IllegalStateException("No value specified for key: " + key);
         }
         Node value = nextNode(valueToken);

         if (keyType == null) {
            keyType = key.getType();
            valueType = value.getType();
         } else if (keyType != key.getType()) {
            throw new IllegalStateException("Mixed type map keys: " + keyType + " and " + key.getType());
         } else if (valueType != value.getType()) {
            throw new IllegalStateException("Mixed type map values: " + valueType + " and " + value.getType());
         }

         map.put(key.evaluate(null), value.evaluate(null));
      }
      // TODO what is the correct behaviour when the map is empty?
      return new ConstantNode(map, mapType(keyType, valueType));
   }

   private boolean notMapEnd(String token) {
      return !isMapEnd(token);
   }

   private boolean isMapEnd(String token) {
      return MAP_END_STRING.equals(token);
   }

   private Node createNode(String token) {
      return readers.entrySet().stream().filter(e -> e.getKey().test(token)).map(Map.Entry::getValue).findFirst()
            .orElseThrow(() -> new IllegalArgumentException(token)).apply(token);
   }

   private VariableNode getVariable(String firstToken) {
      int id = Integer.parseInt(firstToken.substring(1));
      return variableSet.getById(id);
   }

   private ConstantNode createLongConstant(String token) {
      return new ConstantNode(Long.valueOf(token.substring(0, token.length() - 1)), longType());
   }

   private ConstantNode createBigIntegerConstant(String token) {
      return new ConstantNode(toBigInteger(token.substring(0, token.length() - 1)), bigIntegerType());
   }

   private ConstantNode createBigDecimalConstant(String token) {
      return new ConstantNode(toBigDecimal(token.substring(0, token.length() - 1)), bigDecimalType());
   }

   private ConstantNode createDoubleConstant(String token) {
      return new ConstantNode(Double.valueOf(token.substring(0, token.length())), doubleType());
   }

   private ConstantNode createIntegerConstant(String token) {
      return new ConstantNode(Integer.valueOf(token), integerType());
   }

   private boolean isConstant(String token) {
      return getConstant(token) != null;
   }

   private ConstantNode getConstant(String token) {
      for (ConstantNode n : constants) {
         if (token.equals(n.toString())) {
            return n;
         }
      }
      return null;
   }

   private ConstantNode createFunctionConstant(String token) {
      Function function = getFunction(token);
      return new ConstantNode(function, getFunctionType(function));
   }

   private Function getFunction(String token) {
      for (Function f : functions) {
         if (token.equals(f.getDisplayName())) {
            return f;
         }
      }
      throw new IllegalArgumentException("Could not find version of function: " + token + " in: " + Arrays.toString(functions));
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

   /** Returns {@code true} if there is no more data to read from the underlying stream, else {@code false}. */
   public boolean isEndOfStream() throws IOException {
      return cr.isEndOfStream();
   }

   private static Type getFunctionType(Function function) {
      Signature signature = function.getSignature();
      // TODO call signature.getArgumentTypes instead
      Type[] types = new Type[signature.getArgumentTypesLength() + 1];
      types[0] = signature.getReturnType();
      for (int i = 1; i < types.length; i++) {
         types[i] = signature.getArgumentType(i - 1);
      }
      return Type.functionType(types);
   }

   private static boolean isVariable(String token) {
      return token.startsWith("v") && isNumber(token.substring(1));
   }

   private static boolean isLong(String token) {
      return isNumber(token) && token.endsWith("L");
   }

   private static boolean isBigInteger(String token) {
      return isNumber(token) && token.endsWith("I");
   }

   private static boolean isBigDecimal(String token) {
      return isNumber(token) && token.endsWith("D");
   }

   private static boolean isDecimalNumber(String token) {
      return isNumber(token) && token.contains(".");
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

   /**
    * Returns {@code true} if the given {@code String} is suitable for use as the display name of a {@code Function}, else {code false}.
    * <p>
    * A {@code String} is considered suitable as a display name for a {@code Function} - as returned from {@link Function#getDisplayName()} - if it can be
    * successfully parsed by a {@code NodeReader}. Suitable function names do not start with a number or contain any of the special characters used to represent
    * the start or end of functions (i.e. '(' and ')'), lists (i.e. '[' and ']'), maps (i.e. '{' and '}') or strings (i.e. '"').
    */
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
      return c != FUNCTION_END_CHAR && c != FUNCTION_START_CHAR && c != LIST_START_CHAR && c != LIST_END_CHAR && c != MAP_START_CHAR && c != MAP_END_CHAR
            && c != STRING_CHAR && !Character.isWhitespace(c);
   }

   private static BigInteger toBigInteger(String number) {
      switch (number) {
         case "0":
            return BigInteger.ZERO;
         case "1":
            return BigInteger.ONE;
         case "10":
            return BigInteger.TEN;
         default:
            return new BigInteger(number);
      }
   }

   private static BigDecimal toBigDecimal(String number) {
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
         if (previous == -1) {
            return br.read();
         } else {
            int result = previous;
            previous = -1;
            return result;
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
