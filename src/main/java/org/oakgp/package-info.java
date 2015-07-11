/**
 * A tree-based genetic programming framework.
 * <p>
 * <ul>
 * <li>{@link org.oakgp.node} provides classes for constructing tree structures to represent programs.</li>
 * <li>{@link org.oakgp.function} provides classes for implementing the logic associated with function nodes.</li>
 * <li>{@link org.oakgp.primitive} provides classes to construct a primitive set consisting of nodes and functions.</li>
 * <li>{@link org.oakgp.generate} supports the creation of an initial population.</li>
 * <li>{@link org.oakgp.rank} supports ranking of candidates based on their fitness.</li>
 * <li>{@link org.oakgp.select} provides strategies for selecting ranked candidates.</li>
 * <li>{@link org.oakgp.evolve} provides mechanisms for using selected candidates as a basis for evolving new candidates.</li>
 * <li>{@link org.oakgp.terminate} provides predicates for determining when a genetic programming run is complete.</li>
 * </ul>
 */
package org.oakgp;
