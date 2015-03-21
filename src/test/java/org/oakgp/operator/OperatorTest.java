package org.oakgp.operator;

import static java.nio.file.FileVisitResult.CONTINUE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

import org.junit.Test;
import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;

public class OperatorTest {
	@Test
	public void testSimplify() {
		Operator o = new Operator() {
			@Override
			public Signature getSignature() {
				throw new UnsupportedOperationException();
			}

			@Override
			public Object evaluate(Arguments arguments, Assignments assignments) {
				throw new UnsupportedOperationException();
			}
		};
		assertFalse(o.simplify(null).isPresent());
	}

	@Test
	public void test() throws Exception {
		List<Class<?>> operatorClasses = SubClassFinder.find(Operator.class, "src/main/java");
		List<Class<?>> operatorTestClasses = SubClassFinder.find(AbstractOperatorTest.class, "src/test/java");
		for (Class<?> operatorTest : operatorTestClasses) {
			AbstractOperatorTest t = (AbstractOperatorTest) operatorTest.newInstance();
			Class<?> operatorClass = t.getOperator().getClass();
			assertTrue("Tested more than once: " + operatorClass, operatorClasses.contains(operatorClass));
			operatorClasses.remove(operatorClass);
		}
		assertTrue("Not tested " + operatorClasses.toString(), operatorClasses.isEmpty());
	}
}

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
