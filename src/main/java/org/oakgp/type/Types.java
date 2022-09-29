package org.oakgp.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.oakgp.util.PrimitiveMapper;

public final class Types {
   private static final Map<RawTypeKey, Type> RAW_TYPES = new HashMap<>();
   private static final Map<TypeKey, Type> CONCRETE_TYPES = new HashMap<>();

   private Types() {
   }

   public static Type generic(String name, Type... parents) {
      Type generic = new Type(name, new Type[0], true);
      HashSet<Type> parents2 = new HashSet<>(Arrays.asList(parents)); // TODO
      generic.setParents(parents2);
      return generic;
   }

   public static Type declareType(String name) {
      return declareType(name, new Type[0], new Type[0]); // TODO share Type[0]
   }

   public static Type declareType(String name, Type[] parameters, Type[] parents) { // TODO synch here and all public methods
      RawTypeKey key = new RawTypeKey(name, parameters.length);
      Type rawType = RAW_TYPES.get(key);
      if (rawType != null) {
         throw new IllegalStateException("Type already declared: " + name + " with " + parameters.length + " parameters");
         // checkParameters(rawType, parameters);
         // return rawType;
      }

      rawType = new Type(name, parameters, false);
      HashSet<Type> parents2 = new HashSet<>(Arrays.asList(parents)); // TODO
      rawType.setParents(parents2);
      RAW_TYPES.put(new RawTypeKey(name, parameters.length), rawType);
      return rawType;
   }

   public static Type type(String name, Type... parameters) {
      Type raw = RAW_TYPES.get(new RawTypeKey(name, parameters.length)); // TODO check exists
      if (raw == null) {
         throw new IllegalArgumentException("Unknown type: " + name + " with " + parameters.length + " parameters");
      }
      checkParameters(raw, parameters);
      return raw.replace(parameters);

   }

   private static void checkParameters(Type raw, Type... parameters) {
      for (int i = 0; i < parameters.length; i++) {
         if (!parameters[i].isAssignable(raw.parameters[i])) {
            throw new IllegalArgumentException("Parameter mismatch");
            // throw new IllegalArgumentException("Parameter mismatch " + Arrays.toString(parameters) + " " + Arrays.toString(raw.parameters));
         }
      }
   }

   public static Type type(Class<?> target, Type... parameters) {
      target = PrimitiveMapper.mapPrimitive(target);

      TypeVariable<?>[] typeParameters = target.getTypeParameters();
      for (int i = 0; i < typeParameters.length; i++) {
         for (java.lang.reflect.Type bound : typeParameters[i].getBounds()) {
            if (bound.getClass() == Class.class) {
               if (!parameters[i].isAssignable(concreteType((Class<?>) bound))) {
                  throw new RuntimeException(parameters[i] + " is not a subclass of " + bound);
               }
            } else if (bound instanceof ParameterizedType) {
               // TODO
            } else {
               throw new RuntimeException();
            }
         }
      }

      return concreteType(target, parameters);
   }

   private static Type concreteType(Class<?> target, Type... parameters) {
      TypeKey key = new TypeKey(target.getName(), parameters);
      if (CONCRETE_TYPES.containsKey(key)) {
         return CONCRETE_TYPES.get(key);
      }

      Type rawType = rawType(target);
      Type result = rawType.replace(parameters);
      CONCRETE_TYPES.put(key, result); // TODO only cache if doesn't contain generics?
      return result;
   }

   private static Type rawType(Class<?> target) {
      TypeVariable<?>[] typeParameters = target.getTypeParameters();
      RawTypeKey key = new RawTypeKey(target.getName(), target.getTypeParameters().length);
      Type rawType = RAW_TYPES.get(key);
      if (rawType != null) {
         return rawType;
      }

      Type[] parameters = new Type[typeParameters.length];
      rawType = new Type(target.getName(), parameters, false);
      RAW_TYPES.put(key, rawType);

      for (int i = 0; i < parameters.length; i++) {
         TypeVariable<?> typeParameter = typeParameters[i];
         java.lang.reflect.Type[] bounds = typeParameter.getBounds();
         Type[] parameterParents = new Type[bounds.length];
         for (int b = 0; b < bounds.length; b++) {
            parameterParents[b] = loadJavaType(bounds[b], new HashMap<>());
         }
         parameters[i] = generic(typeParameter.getName(), parameterParents);
      }

      rawType.setParents(findParents(target, parameters));
      return rawType;
   }

   private static Set<Type> findParents(Class<?> target, Type... parameters) {
      TypeVariable<?>[] typeParameters = target.getTypeParameters();
      if (typeParameters.length != parameters.length) {
         throw new RuntimeException(target + " " + parameters.length);
      }

      Map<String, Type> parametersMap = new HashMap<>();
      for (int i = 0; i < typeParameters.length; i++) {
         parametersMap.put(typeParameters[i].getName(), parameters[i]);
      }

      Set<Type> parents = new LinkedHashSet<>();

      java.lang.reflect.Type parentClass = target.getGenericSuperclass();
      if (parentClass != null) {
         parents.add(loadJavaType(parentClass, parametersMap));
      }
      for (java.lang.reflect.Type parentInterface : target.getGenericInterfaces()) {
         parents.add(loadJavaType(parentInterface, parametersMap));
      }

      return parents;
   }

   private static Type loadJavaType(java.lang.reflect.Type target, Map<String, Type> parametersMap) {
      if (target.getClass() == Class.class) {
         return rawType((Class<?>) target);
      } else if (target instanceof ParameterizedType) {
         ParameterizedType pt = (ParameterizedType) target;
         Class<?> superClass = (Class<?>) pt.getRawType();
         java.lang.reflect.Type[] superClassArguments = pt.getActualTypeArguments();
         Type[] superClassParameters = new Type[superClassArguments.length];
         for (int i = 0; i < superClassArguments.length; i++) {
            java.lang.reflect.Type arg = superClassArguments[i];
            if (arg.getClass() == Class.class) {
               superClassParameters[i] = rawType((Class<?>) arg);
            } else if (arg instanceof TypeVariable<?>) {
               TypeVariable<?> typeVariable = ((TypeVariable<?>) arg);
               String name = typeVariable.getName();
               superClassParameters[i] = parametersMap.get(name);
               if (superClassParameters[i] == null) { // TODO explain why needed
                  superClassParameters[i] = generic(name);
                  parametersMap.put(name, superClassParameters[i]);
               }
            } else {
               throw new IllegalArgumentException(); // TODO
            }
         }
         return concreteType(superClass, superClassParameters);
      } else {
         throw new IllegalArgumentException(); // TODO
      }
   }

   public static class Type {
      private final String name;
      private final Type[] parameters;
      private final boolean isGeneric;
      private Set<Type> parents;

      private Type(String name, Type[] parameters, boolean isGeneric) {
         this.name = name;
         this.parameters = parameters;
         this.isGeneric = isGeneric;
      }

      void setParents(Set<Type> parents) {
         if (this.parents != null || parents == null) {
            throw new RuntimeException();
         }
         if (!name.equals(Object.class.getName())) {
            parents.add(rawType(Object.class));
         }
         this.parents = Collections.unmodifiableSet(parents);
      }

      public Set<Type> getParents() {
         if (this.parents == null) {
            throw new RuntimeException(name + " " + Arrays.toString(parameters));
         }
         return parents;
      }

      public Type getParameter(int i) {
         return parameters[i];
      }

      public List<Type> getParameters() {
         return Arrays.asList(parameters); // TODO
      }

      public boolean isGeneric() {
         return isGeneric;
      }

      public String getName() {
         return name;
      }

      public Type replace(Type[] newParameters) {
         if (newParameters.length != parameters.length) {
            throw new RuntimeException(Arrays.toString(parameters) + " " + Arrays.toString(newParameters));
         }
         if (parameters.length == 0) {
            return this;
         }

         Map<Type, Type> replacements = new HashMap<>();
         for (int i = 0; i < newParameters.length; i++) {
            replacements.put(this.parameters[i], newParameters[i]);
         }

         return replace(replacements);
      }

      private Type replace(Map<Type, Type> replacements) {
         if (replacements.containsKey(this)) {
            return replacements.get(this);
         }

         boolean replaced = false;

         // replace parameters
         Type[] newParameters = new Type[parameters.length];
         for (int i = 0; i < newParameters.length; i++) {
            if (parameters[i] == null) {
               newParameters[i] = Types.generic(name);
            } else {
               newParameters[i] = parameters[i].replace(replacements);
            }
            if (newParameters[i] != parameters[i]) {
               replaced = true;
            }
         }

         // replace parents
         Set<Type> newParents = new HashSet<>();
         for (Type originalParent : parents == null ? Collections.<Type> emptySet() : parents) {
            Type newParent = originalParent.replace(replacements);
            newParents.add(newParent);
            if (newParent != originalParent) {
               replaced = true;
            }
         }

         // return new type if anything has been replaced, else return original
         if (replaced) {
            TypeKey key = new TypeKey(this.name, newParameters);
            if (CONCRETE_TYPES.containsKey(key)) {
               return CONCRETE_TYPES.get(key);
            }
            Type newType = new Type(this.name, newParameters, false);
            newType.setParents(newParents);
            CONCRETE_TYPES.put(key, newType);
            return newType;
         } else {
            return this;
         }
      }

      public boolean isAssignable(Type toAssignTo) {
         if (isGeneric && toAssignTo.isGeneric) {
            // throw new RuntimeException(); // TODO
            return true;
         }

         if (toAssignTo == this) {
            return true;
         } else if (!isGeneric && !toAssignTo.isGeneric && name.equals(toAssignTo.name)) {
            if (parameters.length != toAssignTo.parameters.length) {
               return false;
            }

            for (int i = 0; i < parameters.length; i++) {
               if (!parameters[i].isAssignable(toAssignTo.parameters[i])) {
                  return false;
               }
            }
            return true;
         } else if (toAssignTo.isGeneric) {
            for (Type p : toAssignTo.parents) {
               if (!_isAssignable(this, p)) {
                  return false;
               }
            }
            return true;
         } else {
            for (Type p : parents) {
               if (p.isAssignable(toAssignTo)) {
                  return true;
               }
            }
            return false;
         }
      }

      // can "from" be assigned to "to"
      // are any parents of "from" assignable to "to"?
      private static boolean _isAssignable(Type from, Type to) {
         if (from == to) {
            return true;
         }

         for (Type p : from.parents) {
            if (p.isAssignable(to)) {
               return true;
            }
            if (_isAssignable(p, to)) {
               return true;
            }
         }
         return false;
      }

      @Override
      public String toString() {
         StringBuilder sb = new StringBuilder();
         sb.append(name);
         if (parameters.length > 0) {
            sb.append('<');
            for (int i = 0; i < parameters.length; i++) {
               if (i != 0) {
                  sb.append(',');
               }
               sb.append(parameters[i]);
            }
            sb.append('>');
         }
         return sb.toString();
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         }
         if (isGeneric) {
            return false;
         }
         if (obj == null) {
            return false;
         }
         if (getClass() != obj.getClass()) {
            return false;
         }

         Type other = (Type) obj;
         return name.equals(other.name) && Arrays.equals(parameters, other.parameters);
      }

      @Override
      public int hashCode() {
         return name.hashCode() * Arrays.hashCode(parameters);
      }
   }
}
