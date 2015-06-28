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
package org.oakgp.select;

import org.oakgp.node.Node;

/**
 * Used to obtain {@code Node} instances.
 * <p>
 * The strategy to determine what is returned, and in what order, will depend on the specific implementation of {@code NodeSelector} that is being used.
 */
@FunctionalInterface
public interface NodeSelector {
   /**
    * Returns a {@code Node}.
    *
    * @return a {@code Node}
    */
   Node next();
}
