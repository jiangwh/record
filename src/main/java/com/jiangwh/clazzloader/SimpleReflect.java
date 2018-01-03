package com.jiangwh.clazzloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SimpleReflect {

	public static Object invoke(Class<?> clazz, Method method, Object... args)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		Object object = clazz.newInstance();
		return method.invoke(object, args);
	}
}
