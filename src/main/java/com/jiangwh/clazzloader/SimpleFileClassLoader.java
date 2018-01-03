package com.jiangwh.clazzloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLClassLoader;

public class SimpleFileClassLoader {

	public static Class<?> loadJavaCode(String javaFilePath, String javaFileName) throws ClassNotFoundException {
		URL url = ClassLoader.getSystemResource(javaFilePath);
		URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { url });
		Class<?> clazz = classLoader.loadClass(javaFileName);
		return clazz;
	}
	
	public static String getFileName(String javaFilePath) throws FileNotFoundException {
		File file = new File(javaFilePath);
		if (file.exists() && file.isFile()) {
			return file.getName();
		} else {
			throw new FileNotFoundException(javaFilePath);
		}
	}



	public static void main(String[] args) throws Exception {
		String path = "/Users/jiangwh/work/note/record/src/main/java/Test.java";
		Class<?> clazz = loadJavaCode(path, getFileName(path).replace(".java", ""));
		System.out.println(SimpleReflect.invoke(clazz, clazz.getMethod("main", String[].class), (Object) new String[] {}));
	}
}
