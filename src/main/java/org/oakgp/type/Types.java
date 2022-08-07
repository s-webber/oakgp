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
import java.util.LinkedHashSet;
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
   private static final Type OBJECT = declareType("Object");

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

   private static LinkedHashSet<Type> getParents(Type[] parents) {
      LinkedHashSet<Type> result = new LinkedHashSet<>();
      if (OBJECT != null) { // Until OBJECT is declared then OBJECT will be null
         result.add(OBJECT);
      }
      for (Type p : parents) {
         result.add(p);
      }
      return result;
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

   private static LinkedHashSet<Type> getParameterisedType(String name, Type[] parameters) {
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

      LinkedHashSet<Type> parents = new LinkedHashSet<>();
      for (Type child : template.parents) {
         parents.add(replace(child, assignments));
      }
      return parents;
   }

   // TODO move to separate class?
   public static void match(Type template, Type actual, Map<Type, Type> assignments) {
      template = assignments.getOrDefault(template, template);
      if (template.template) {
         if (!actual.isAssignable(template)) {
            throw new IllegalArgumentException(actual + " not of types " + template.hierarchy);
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
      return new Type(name, getParents(parents), true);
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
      private final Set<Type> rename; // TODO
      private final Set<Type> parents;
      private final Type[] parameters;
      private final boolean template;
      private final Set<Type> hierarchy;

      // NOTE: only reason for using LinkedHashSet is so that order is predictable when comparing error messages in unit tests
      private Type(String name, LinkedHashSet<Type> parents, Type... parameters) {
         this(name, parents, parameters, false);
      }

      private Type(String name, LinkedHashSet<Type> parents, boolean template) {
         this(name, parents, EMPTY_ARRAY, template);
      }

      private Type(String name, Set<Type> parents, Type[] parameters, boolean template) {
         this.name = name;
         for (Type p : parents) {
            if (p.template) {
               throw new IllegalArgumentException();
            }
         }
         this.rename = template ? Collections.unmodifiableSet(parents) : Collections.singleton(this);
         this.parents = Collections.unmodifiableSet(buildParents(parents));
         this.parameters = Arrays.copyOf(parameters, parameters.length);
         this.template = template;
         this.hierarchy = Collections.unmodifiableSet(buildHierarchy(parents));
      }

      private Set<Type> buildHierarchy(Set<Type> parents) {
         Set<Type> result = new LinkedHashSet<>();
         if (!template) {
            result.add(this);
         }
         result.addAll(buildParents(parents));
         return result;
      }

      private Set<Type> buildParents(Set<Type> parents) {
         Set<Type> result = new LinkedHashSet<>();
         for (Type p : parents) {
            result.addAll(p.hierarchy);
         }
         return result;
      }

      public String getName() {
         return name;
      }

      public Set<Type> getParents() { // TODO remove "parents" and always use "hierarchy"
         return parents;
      }

      public List<Type> getParameters() {
         return Arrays.asList(parameters);
      }

      public Type getParameter(int index) {
         return parameters[index];
      }

      /** Is this instance assignable to the given {@code type}? */
      public boolean isAssignable(Type toAssignTo) { // TODO use or remove
         for (Type toAssignToParent : toAssignTo.rename) {
            if (!c(toAssignToParent)) {
               return false;
            }
         }
         return true;
      }

      private boolean c(Type toAssignToParent) {
         for (Type toAssignHierarchy : hierarchy) {
            if (isAssignable(toAssignHierarchy, toAssignToParent)) {
               return true;
            }
         }
         return false;
      }

      private static boolean isAssignable(Type toAssign, Type toAssignTo) {
         // TODO use TemplateKey.equals()
         if (!toAssign.getName().equals(toAssignTo.getName()) || toAssign.getParameters().size() != toAssignTo.getParameters().size()) {
            return false;
         }
         for (int i = 0; i < toAssign.getParameters().size(); i++) {
            if (!toAssign.getParameters().get(i).isAssignable(toAssignTo.getParameters().get(i))) {
               return false;
            }
         }
         return true;
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
