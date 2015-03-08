package org.oakgp.selector;

import java.util.List;

import org.oakgp.RankedCandidate;

/** Returns instances of {@code NodeSelector}. */
@FunctionalInterface
public interface NodeSelectorFactory {
	/**
	 * Returns a {@code NodeSelector} that selects from the specified candidates.
	 *
	 * @param candidates
	 *            a list of {@code RankedCandidate} instances the returned selector should select from
	 * @return a {@code NodeSelector} that selects from {@code candidates}
	 */
	NodeSelector getSelector(List<RankedCandidate> candidates);
}
