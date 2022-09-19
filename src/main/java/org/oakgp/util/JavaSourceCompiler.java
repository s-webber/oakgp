package org.oakgp.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/** Compiles Java source code into bytecode. */
final class JavaSourceCompiler {
   private final Object lock = new Object();
   private final JavaCompiler compiler;
   private final SimpleJavaFileManager fileManager;
   private final CompiledClassLoader classLoader = new CompiledClassLoader();
   /** Java source code generated at runtime. */
   private final List<SourceJavaFileObject> sourceFiles = new ArrayList<>();
   /** Java class files generated at runtime. Key = class name. */
   private final Map<String, ClassJavaFileObject> classFiles = new HashMap<>();

   JavaSourceCompiler() {
      compiler = ToolProvider.getSystemJavaCompiler();
      StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(null, Locale.ENGLISH, null);
      fileManager = new SimpleJavaFileManager(standardFileManager);
   }

   /**
    * Compiles the specified java source code and returns the resulting Class object.
    *
    * @param className class name to compile
    * @param sourceCode java source code to compile
    * @return the newly compiled class
    * @throws ProjogException
    */
   Class<?> compileClass(String className, String sourceCode) {
      synchronized (lock) {
         try {
            // To view generated code uncomment: Files.write(new File("target", className + ".txt").toPath(), sourceCode.getBytes());
            return compile(className, sourceCode);
         } catch (Exception e) {
            throw new RuntimeException("Cannot compile: " + className + " source: " + sourceCode, e);
         }
      }
   }

   private Class<?> compile(String className, String sourceCode) throws ClassNotFoundException {
      DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
      sourceFiles.add(new SourceJavaFileObject(className, sourceCode));
      CompilationTask compilationTask = compiler.getTask(null, fileManager, diagnostics, null, null, sourceFiles);

      boolean status = compilationTask.call(); // invoke compilation
      if (!status) {
         // If compilation error occurs then throw exception with as much information as possible
         StringBuilder reasons = new StringBuilder();
         for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
            reasons.append(" Error on line " + diagnostic.getLineNumber() + " in " + diagnostic);
         }
         throw new RuntimeException("Failed compilation of " + className + reasons);
      }

      return classLoader.loadClass(className);
   }

   private class SimpleJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
      SimpleJavaFileManager(JavaFileManager fileManager) {
         super(fileManager);
      }

      @Override
      public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
         ClassJavaFileObject classFile = new ClassJavaFileObject(className, kind);
         if (!classFiles.containsKey(className)) {
            classFiles.put(className, classFile);
         }
         return classFile;
      }
   }

   private class CompiledClassLoader extends ClassLoader {
      @Override
      protected Class<?> findClass(String name) throws ClassNotFoundException {
         ClassJavaFileObject classFile = classFiles.get(name);

         if (classFile == null) {
            return super.findClass(name);
         }

         if (classFile.compiledClass == null) {
            byte[] bytes = classFile.outputStream.toByteArray();
            Class<?> compiledClass = super.defineClass(name, bytes, 0, bytes.length);
            classFile.compiledClass = compiledClass;
            classFile.outputStream = null;
         }
         return classFile.compiledClass;
      }
   }

   private static class SourceJavaFileObject extends SimpleJavaFileObject {
      final String sourceCode;

      SourceJavaFileObject(String className, String sourceCode) {
         super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
         this.sourceCode = sourceCode;
      }

      @Override
      public CharSequence getCharContent(boolean ignoreEncodingErrors) {
         return sourceCode;
      }
   }

   private static class ClassJavaFileObject extends SimpleJavaFileObject {
      ByteArrayOutputStream outputStream;
      Class<?> compiledClass;

      ClassJavaFileObject(String className, Kind kind) {
         super(URI.create("mem:///" + className.replace('.', '/') + kind.extension), kind);
         outputStream = new ByteArrayOutputStream();
      }

      @Override
      public OutputStream openOutputStream() {
         return outputStream;
      }
   }
}
