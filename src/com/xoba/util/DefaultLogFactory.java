package com.xoba.util;

import java.util.HashMap;
import java.util.Map;

import com.xoba.util.LogFactory.MyLogger;

public final class DefaultLogFactory implements ILogFactory {

	private final Map<String, ILogger> loggers = new HashMap<String, ILogger>();

	@Override
	public ILogger create(Class<?> clazz) {
		return create(clazz.getName(), clazz.getSimpleName());
	}

	@Override
	public ILogger create() {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		String name = stack[2].getClassName();
		return create(name, extractAbbreviatedName(name));
	}

	private static String extractAbbreviatedName(String className) {
		String[] parts = className.split("\\.");
		return parts[parts.length - 1];
	}

	public static void main(String... args) throws Exception {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		for (StackTraceElement e : stack) {
			String name = e.getClassName();
			System.out.println(name + "; " + Class.forName(name));
		}
	}

	public ILogger create(String name) {
		return create(name, name);
	}

	@Override
	public ILogger create(String fullName, String abbreviatedName) {
		if (!loggers.containsKey(fullName)) {
			loggers.put(fullName, new MyLogger(abbreviatedName));
		}
		return loggers.get(fullName);
	}
}