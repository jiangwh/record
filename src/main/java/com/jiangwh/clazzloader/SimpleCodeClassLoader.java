package com.jiangwh.clazzloader;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class SimpleCodeClassLoader {

	class CharSequenceJavaFileObject extends SimpleJavaFileObject {

		private CharSequence content;

		public CharSequenceJavaFileObject(String className, CharSequence content) {
			super(URI.create("string:///" + className.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension),
					JavaFileObject.Kind.SOURCE);
			this.content = content;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return content;
		}
	}

	class JavaClassObject extends SimpleJavaFileObject {

		protected final ByteArrayOutputStream bos = new ByteArrayOutputStream();

		public JavaClassObject(String name, JavaFileObject.Kind kind) {
			super(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind);
		}

		public byte[] getBytes() {
			return bos.toByteArray();
		}

		@Override
		public OutputStream openOutputStream() throws IOException {
			return bos;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	class ClassFileManager extends ForwardingJavaFileManager {
		private List<JavaClassObject> javaClassObjectList;

		public ClassFileManager(StandardJavaFileManager standardManager) {
			super(standardManager);
			this.javaClassObjectList = new ArrayList<JavaClassObject>();
		}

		public JavaClassObject getMainJavaClassObject() {
			if (this.javaClassObjectList != null && this.javaClassObjectList.size() > 0) {
				int size = this.javaClassObjectList.size();
				return this.javaClassObjectList.get((size - 1));
			}
			return null;
		}

		public List<JavaClassObject> getInnerClassJavaClassObject() {
			if (this.javaClassObjectList != null && this.javaClassObjectList.size() > 0) {
				int size = this.javaClassObjectList.size();
				if (size == 1) {
					return null;
				}
				return this.javaClassObjectList.subList(0, size - 1);
			}
			return null;
		}

		@Override
		public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind,
				FileObject sibling) throws IOException {
			JavaClassObject jclassObject = new JavaClassObject(className, kind);
			this.javaClassObjectList.add(jclassObject);
			return jclassObject;
		}

	}

	class DynamicClassLoader extends URLClassLoader {
		public DynamicClassLoader(ClassLoader parent) {
			super(new URL[0], parent);
		}

		public DynamicClassLoader(URL[] urls) {
			super(urls);
		}

		public Class<?> findClassByClassName(String className) throws ClassNotFoundException {
			return this.findClass(className);
		}

		public Class<?> loadClass(String fullName, JavaClassObject jco) {
			byte[] classData = jco.getBytes();
			return this.defineClass(fullName, classData, 0, classData.length);
		}
	}

	public Class<?> loadJavaCode(String fullClassName, String javaCode) throws IOException {
		Class<?> clazz = null;
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		ClassFileManager fileManager = new ClassFileManager(compiler.getStandardFileManager(diagnostics, null, null));
		List<JavaFileObject> jfiles = new ArrayList<JavaFileObject>();
		jfiles.add(new CharSequenceJavaFileObject(fullClassName, javaCode));
		List<String> options = new ArrayList<String>();
		options.add("-encoding");
		options.add("UTF-8");
		options.add("-classpath");
		options.add(".");
		options.add("-parameters");

		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, jfiles);
		boolean success = task.call();
		if (success) {
			JavaClassObject jco = fileManager.getMainJavaClassObject();
			DynamicClassLoader urlClassLoader = new DynamicClassLoader(new URL[] {});
			try {
				List<JavaClassObject> innerClassJcos = fileManager.getInnerClassJavaClassObject();
				if (innerClassJcos != null && innerClassJcos.size() > 0) {
					for (JavaClassObject inner : innerClassJcos) {
						String name = inner.getName();
						name = name.substring(1, name.length() - 6).replace("/", ".");
						urlClassLoader.loadClass(name, inner);
					}
				}
				clazz = urlClassLoader.loadClass(fullClassName, jco);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (urlClassLoader != null) {
					urlClassLoader.close();
				}
			}
		} else {
			StringBuilder error = new StringBuilder();
			for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
				error.append(compilePrint(diagnostic));
			}
		}

		return clazz;
	}

	private StringBuilder compilePrint(Diagnostic<?> diagnostic) {
		StringBuilder res = new StringBuilder();
		res.append("Code:[" + diagnostic.getCode() + "]\n");
		res.append("Kind:[" + diagnostic.getKind() + "]\n");
		res.append("Position:[" + diagnostic.getPosition() + "]\n");
		res.append("Start Position:[" + diagnostic.getStartPosition() + "]\n");
		res.append("End Position:[" + diagnostic.getEndPosition() + "]\n");
		res.append("Source:[" + diagnostic.getSource() + "]\n");
		res.append("Message:[" + diagnostic.getMessage(null) + "]\n");
		res.append("LineNumber:[" + diagnostic.getLineNumber() + "]\n");
		res.append("ColumnNumber:[" + diagnostic.getColumnNumber() + "]\n");
		return res;
	}

	public static String findPackage(String sourceCode) throws IOException {
		BufferedReader br = new BufferedReader(new StringReader(sourceCode));
		try {
			String temp = null;
			while ((temp = br.readLine()) != null) {
				if (isBlank(temp)) {
					continue;
				}
				if (temp.trim().startsWith("package ")) {
					temp = temp.split("[\\s+?;]")[1];
					return temp;
				} else {
					return null;
				}
			}

			return null;
		} finally {
			if (br != null) {
				br.close();
			}
		}
	}

	public static String findClassName(String sourceCode) {

		try (BufferedReader br = new BufferedReader(new StringReader(sourceCode))) {
			String temp = null;

			while ((temp = br.readLine()) != null) {
				if (isBlank(temp)) {
					continue;
				}
				Matcher matcher = Pattern.compile("^(.*)(public\\s+?class\\s+?)(.*?)(\\{.*)$").matcher(temp);
				if (matcher.find()) {
					return matcher.group(3).trim();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static boolean isBlank(CharSequence cs) {
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) throws IOException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, InstantiationException, NoSuchMethodException, SecurityException {
		String code = "package com.test; public class     Test {public static void main(String[] args) {String hello = \"Hello World!!!\";System.out.println(hello);}}";
		String className = findPackage(code).concat(".").concat(findClassName(code));
		Class<?> clazz = new SimpleCodeClassLoader().loadJavaCode(className, code);
		SimpleReflect.invoke(clazz, clazz.getMethod("main", String[].class), (Object) new String[] {});
	}
}
