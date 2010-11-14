package com.xoba.util;

public interface ILogger {

	public void debugf(String fmt, Object... args);

	public void warnf(String fmt, Object... args);

	public void infof(String fmt, Object... args);

	public void errorf(String fmt, Object... args);

	public void debug(Object msg);

	public void warn(Object msg);

	public void info(Object msg);

	public void warn(String fmt, Throwable t, Object... args);

}