package com.xoba.util;

public interface ILogFactory {

	public ILogger create(Class<?> clazz);

	public ILogger create(String name, String abbreviation);

	public ILogger create();

}
