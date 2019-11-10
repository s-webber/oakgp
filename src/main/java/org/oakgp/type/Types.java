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
package org.oakgp.type;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contains definitions of data types.
 * <p>
 * Used to facilitate strongly typed genetic programming (STGP).
 */
public final class Types {
   private static final Map<TemplateKey, Type> TEMPLATE_CACHE = new HashMap<>();
   private static final Map<TypeKey, Type> TYPE_CACHE = new HashMap<>();
   private static final Type[] EMPTY_ARRAY = new Type[0];

   public static Type declareType(String name) {
      return declareType(name, EMPTY_ARRAY, EMPTY_ARRAY);
   }

   public synchronized static Type declareType(String name, Type[] parents, Type[] parameters) {
      TemplateKey templateKey = new TemplateKey(name, parameters.length);
      if (TEMPLATE_CACHE.containsKey(templateKey)) {
         throw new IllegalStateException("Type already declared: " + name + " with " + parameters.length + " parameters");
      }

      Type type = new Type(name, getParents(parents), parameters);
      TEMPLATE_CACHE.put(templateKey, type);
      TYPE_CACHE.put(new TypeKey(name, parameters), type);
      return type;
   }

   private static Set<Type> getParents(Type[] parents) {
      return parents.length == 0 ? Collections.emptySet() : new HashSet<>(Arrays.asList(parents));
   }

   public synchronized static Type type(String name, Type... parameters) {
      // Did try using TYPE_CACHE.computeIfAbsent here but that caused intermittent unexpected behaviour.
      // The call to getParameterisedType can result in TYPE_CACHE being updated. The HashMap
      // implementation of computeIfAbsent can cause problems if a side-effect of the mappingFunction
      // is an insert into the map.
      TypeKey key = new TypeKey(name, parameters);
      Type type = TYPE_CACHE.get(key);
      if (type == null) {
         type = new Type(name, getParameterisedType(name, parameters), parameters);
         TYPE_CACHE.put(key, type);
      }
      return type;
   }

   private static Set<Type> getParameterisedType(String name, Type[] parameters) {
      Type template = TEMPLATE_CACHE.get(new TemplateKey(name, parameters.length));
      if (template == null) {
         throw new IllegalArgumentException("Unknown type: " + name + " with " + parameters.length + " parameters");
      }

      if (template.parameters.length != parameters.length) {
         throw new IllegalArgumentException("Parameter length mismatch");
      }

      Map<Type, Type> assignments = new HashMap<>();
      for (int i = 0; i < parameters.length; i++) {
         match(template.parameters[i], parameters[i], assignments);
      }

      Set<Type> parents = new HashSet<>();
      for (Type child : template.parents) {
         parents.add(replace(child, assignments));
      }
      return parents;
   }

   // TODO move to separate class?
   public static void match(Type template, Type actual, Map<Type, Type> assignments) {
      template = assignments.getOrDefault(template, template);
      if (template.template) {
         if (!actual.hierarchy.containsAll(template.hierarchy)) {
            throw new IllegalArgumentException(actual.name + " not of types " + template.hierarchy);
         }
         assignments.put(template, actual);
         return;
      }

      if (actual.template) {
         if (!template.hierarchy.containsAll(actual.hierarchy)) {
            throw new RuntimeException(actual.name + " " + actual.hierarchy + " " + template.name + " " + template.hierarchy);
         }
         assignments.put(actual, template);
         actual = template;
      } else {
         if (!template.name.equals(actual.name)) {
            throw new IllegalArgumentException("Parameter mismatch");
         }

         if (template.parameters.length != actual.parameters.length) {
            throw new RuntimeException();
         }
      }

      for (int i = 0; i < template.parameters.length; i++) {
         match(template.parameters[i], actual.parameters[i], assignments);
      }
   }

   private static Type replace(Type child, Map<Type, Type> assignments) {
      if (assignments.containsKey(child)) {
         return assignments.get(child);
      }

      Type[] parameters = new Type[child.parameters.length];
      for (int i = 0; i < parameters.length; i++) {
         parameters[i] = replace(child.parameters[i], assignments);
      }
      return type(child.name, parameters);
   }

   public static Type generic(String name, Type... parents) {
      return new Type(name, new HashSet<>(Arrays.asList(parents)), true);
   }

   private static class TemplateKey {
      private final String name;
      private final int arity;

      private TemplateKey(String name, int arity) {
         this.name = name;
         this.arity = arity;
      }

      @Override
      public boolean equals(Object o) {
         TemplateKey k = (TemplateKey) o;
         return name.equals(k.name) && arity == k.arity;
      }

      @Override
      public int hashCode() {
         return name.hashCode();
      }
   }

   /**
    * Represents a data type.
    * <p>
    * e.g. integer, boolean, string, list, map, function.
    */
   public static class Type {
      private final String name;
      private final Set<Type> parents;
      private final Type[] parameters;
      private final boolean template;
      private final Set<Type> hierarchy;

      private Type(String name, Set<Type> parents, Type... parameters) {
         this(name, parents, parameters, false);
      }

      private Type(String name, Set<Type> parents, boolean template) {
         this(name, parents, EMPTY_ARRAY, template);
      }

      private Type(String name, Set<Type> parents, Type[] parameters, boolean template) {
         this.name = name;
         this.parents = Collections.unmodifiableSet(parents);
         this.parameters = Arrays.copyOf(parameters, parameters.length);
         this.template = template;
         this.hierarchy = Collections.unmodifiableSet(buildHierarchy());
      }

      public String getName() {
         return name;
      }

      public Set<Type> getParents() {
         return parents;
      }

      public List<Type> getParameters() {
         return Arrays.asList(parameters);
      }

      private Set<Type> buildHierarchy() { // TODO calculate in constructor and cache
         Set<Type> result = new HashSet<>();
         if (!template) {
            result.add(this);
         }
         for (Type p : parents) {
            result.addAll(p.buildHierarchy());
         }
         return result;
      }

      /** Is this instance assignable to the given {@code type}? */
      public boolean isAssignable(Type type) {
         return this == type || hierarchy.containsAll(type.hierarchy);
      }

      public boolean isGeneric() {
         return template;
      }

      @Override
      public String toString() {
         if (parameters.length == 0) {
            return name;
         } else {
            return name + " " + Arrays.toString(parameters);
         }
      }
   }
}
