package com.xoba.mra;

public interface ILogger {

	public void debugf(String fmt, Object... args);

	public void warnf(String fmt, Object... args);

	public void warn(String fmt, Throwable t, Object... args);

	public void errorf(String fmt, Object... args);

}