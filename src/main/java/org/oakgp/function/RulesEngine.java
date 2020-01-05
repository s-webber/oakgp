/*
 * Copyright 2019 S. Webber
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
package org.oakgp.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.oakgp.node.Node;

public class RulesEngine {
   private final Map<Node, Boolean> facts = new HashMap<>();
   private final Map<Node, List<Rule>> rules = new HashMap<>();

   public void addFact(Node fact, boolean value) {
      Boolean previousValue = facts.get(fact);
      if (previousValue == null) {
         // System.out.println(fact + " is " + value);
         facts.put(fact, value);
         fireRules(fact, value);
      } else if (!previousValue.equals(value)) {
         throw new RuntimeException(fact + " " + previousValue + " " + value);
      }
   }

   private void fireRules(Node fact, boolean value) {
      List<Rule> r = rules.get(fact);
      if (r != null) {
         for (int i = 0; i < r.size(); i++) {
            r.get(i).deduce(this, fact, value);
         }
      }
   }

   public void addRule(Node fact, Rule rule) {
      rules.compute(fact, (k, v) -> {
         if (v == null) {
            v = new ArrayList<>();
         }
         v.add(rule);
         return v;
      });
   }

   public void printFacts() {
      System.out.println("FACTS");
      for (Entry<Node, Boolean> e : facts.entrySet()) {
         System.out.println(e.getKey() + " = " + e.getValue());
      }
      for (Entry<Node, List<Rule>> e : rules.entrySet()) {
         System.out.println(e.getKey() + " = " + e.getValue().size() + " rules");
      }
      System.out.println();
   }

   public boolean hasFact(Node fact) {
      return facts.containsKey(fact);
   }

   // TODO return boolean primitive
   public Boolean getFact(Node fact) {
      return facts.get(fact);
   }

   public void addEngine(RulesEngine other) {
      for (Entry<Node, List<Rule>> e : other.rules.entrySet()) {
         for (Rule r : e.getValue()) {
            addRule(e.getKey(), r);
         }
      }
   }

   public RulesEngine copy(Node fact, boolean value) {
      RulesEngine copy = copy();
      copy.addFact(fact, value);
      return copy;
   }

   public RulesEngine copy() {
      RulesEngine copy = new RulesEngine();
      copy.facts.putAll(this.facts);
      for (Entry<Node, List<Rule>> e : this.rules.entrySet()) {
         copy.rules.put(e.getKey(), new ArrayList<>(e.getValue()));
      }
      return copy;
   }

   public Map<Node, Boolean> getCommonFacts(RulesEngine input) {
      Map<Node, Boolean> result = new HashMap<>();
      for (Entry<Node, Boolean> e : facts.entrySet()) {
         Node fact = e.getKey();
         if (input.hasFact(fact) && e.getValue().equals(input.getFact(fact))) {
            result.put(fact, e.getValue());
         }
      }
      return result;
   }

   public Map<Node, Boolean> getFacts() {
      return new HashMap<>(facts);
   }
}
