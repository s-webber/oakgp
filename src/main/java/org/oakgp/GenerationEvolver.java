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
package org.oakgp;

import static java.lang.Math.min;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.oakgp.node.Node;
import org.oakgp.select.NodeSelector;
import org.oakgp.select.NodeSelectorFactory;
import org.oakgp.util.NodeSet;

/** Creates a new generation of {@code Node} instances evolved from an existing generation. */
public final class GenerationEvolver { // TODO convert to an interface
   private final int elitismSize;
   private final NodeSelectorFactory selectorFactory;
   private final Map<GeneticOperator, Long> operators;

   public GenerationEvolver(int elitismSize, NodeSelectorFactory selectorFactory, Map<GeneticOperator, Long> operators) {
      this.elitismSize = elitismSize;
      this.selectorFactory = selectorFactory;
      this.operators = operators;
   }

   /**
    * Returns a new generation of {@code Node} instances evolved from the specified existing generation.
    *
    * @param oldGeneration
    *           the existing generation to use as a basis for evolving a new generation
    * @return a new generation of {@code Node} instances evolved from the existing generation specified by {@code oldGeneration}
    */
   public Collection<Node> process(List<RankedCandidate> oldGeneration) {
      NodeSelector selector = selectorFactory.getSelector(oldGeneration);
      Set<Node> newGeneration = new NodeSet();

      final int elitismSizeForGeneration = min(elitismSize, oldGeneration.size());
      for (int i = 0; i < elitismSizeForGeneration; i++) {
         newGeneration.add(oldGeneration.get(i).getNode());
      }

      for (Map.Entry<GeneticOperator, Long> e : operators.entrySet()) {
         GeneticOperator operator = e.getKey();
         long count = e.getValue();
         for (int i = 0; i < count; i++) {
            newGeneration.add(operator.evolve(selector));
         }
      }

      return newGeneration;
   }
}
