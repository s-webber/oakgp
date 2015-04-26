package org.oakgp;

import org.oakgp.node.Node;

public interface TreeGenerator {
   Node generate(Type type, int depth);
}
