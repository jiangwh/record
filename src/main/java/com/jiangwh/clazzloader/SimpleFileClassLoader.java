package com.jiangwh.clazzloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class SimpleFileClassLoader {

	public static Class<?> loadJavaCode(String javaFilePath, String javaFileName) throws ClassNotFoundException, MalformedURLException {
		URL url = ClassLoader.getSystemResource(javaFilePath);
		if(null==url){
			File file =new File(javaFilePath);
			url = file.toURI().toURL();
		}
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
		String path = "/Users/jiangwh/work/note/record/src/main/java/com/jiangwh/test/Test.java";
		path  = "/Users/jiangwh/work/note/record/src/main/java/B.java";
		Class<?> clazz = loadJavaCode(path,
//				"com.jiangwh.test.Test");
				getFileName(path).replace(".java", ""));
		
		SimpleReflect.invoke(clazz, clazz.getMethod("deal", String[].class), (Object) new String[] {});
		
	}
}
