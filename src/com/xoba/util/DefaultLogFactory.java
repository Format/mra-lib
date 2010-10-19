package com.xoba.util;

import java.util.HashMap;
import java.util.Map;

import com.xoba.util.LogFactory.MyLogger;

public final class DefaultLogFactory implements ILogFactory {

	private final Map<String, ILogger> loggers = new HashMap<String, ILogger>();

	public ILogger create(Class<?> clazz) {
		return create(clazz.getName(), clazz.getSimpleName());
	}

	public ILogger create() {
		try {
			StackTraceElement[] stack = Thread.currentThread().getStackTrace();
			return create(Class.forName(stack[2].getClassName()));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public ILogger create(String name) {
		return create(name, name);
	}

	public ILogger create(String name, String abbreviation) {
		if (!loggers.containsKey(name)) {
			loggers.put(name, new MyLogger(abbreviation));
		}
		return loggers.get(name);
	}
}