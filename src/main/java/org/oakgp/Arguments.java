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
package org.oakgp;

/**
 * Represents an ordered collection of arguments used in the evaluation of a function.
 *
 * @see org.oakgp.function.Function#evaluate(Arguments)
 */
public interface Arguments {
   /**
    * Returns the element at the specified position in this {@code Arguments}.
    *
    * @param index
    *           index of the element to return
    * @return the element at the specified position in this {@code Arguments}
    * @throws ArrayIndexOutOfBoundsException
    *            if the index is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>)
    */
   <T> T getArg(int index);

   /**
    * Returns the number of elements in this {@code Arguments}.
    *
    * @return the number of elements in this {@code Arguments}
    */
   int size();

   /** Returns the first element of this {@code Arguments}. */
   default <T> T first() {
      return getArg(0);
   }

   /** Returns the second element of this {@code Arguments}. */
   default <T> T second() {
      return getArg(1);
   }

   /** Returns the third element of this {@code Arguments}. */
   default <T> T third() {
      return getArg(2);
   }
}
