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
package org.oakgp.function;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Tree-walks a directory to find all classes assignable from (i.e. extend or implement) a specified class. */
class SubClassFinder extends SimpleFileVisitor<Path> {
   private final Class<?> parentClass;
   private final List<Class<?>> result = new ArrayList<>();

   static List<Class<?>> find(Class<?> parentClass, String directoryName) throws IOException {
      SubClassFinder w = new SubClassFinder(parentClass);
      Files.walkFileTree(new File(directoryName).toPath(), w);
      return w.result;
   }

   SubClassFinder(Class<?> parentClass) {
      this.parentClass = parentClass;
   }

   @Override
   public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
      File file = path.toFile();
      Optional<Class<?>> optional = getSubClass(file);
      if (optional.isPresent()) {
         result.add(optional.get());
      }
      return CONTINUE;
   }

   private Optional<Class<?>> getSubClass(File file) {
      if (!isJavaSource(file)) {
         return Optional.empty();
      }

      String className = getClassName(file);
      Class<?> c = createClass(className);
      if (isSubClass(c)) {
         return Optional.of(c);
      } else {
         return Optional.empty();
      }
   }

   private Class<?> createClass(String className) {
      try {
         return Class.forName(className);
      } catch (ClassNotFoundException e) {
         throw new RuntimeException("Could not create " + className, e);
      }
   }

   private boolean isSubClass(Class<?> c) {
      return isConcrete(c) && isAssignableFromParentClass(c);
   }

   private boolean isConcrete(Class<?> c) {
      return !Modifier.isAbstract(c.getModifiers());
   }

   private boolean isAssignableFromParentClass(Class<?> c) {
      return parentClass.isAssignableFrom(c);
   }

   private boolean isJavaSource(File f) {
      String name = f.getName();
      return name.endsWith(".java") && !name.equals("package-info.java");
   }

   private String getClassName(File javaFile) {
      String filePath = removeFileExtension(javaFile.getPath()).replace(File.separatorChar, '.');
      int packagePos = filePath.lastIndexOf("org.oakgp");
      return filePath.substring(packagePos, filePath.length());
   }

   private String removeFileExtension(String fileName) {
      int extensionPos = fileName.lastIndexOf('.');
      if (extensionPos == -1) {
         return fileName;
      } else {
         return fileName.substring(0, extensionPos);
      }
   }
}
