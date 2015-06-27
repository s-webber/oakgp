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
