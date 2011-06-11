package com.xoba.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.TimeZone;

public class LogFactory {

	public synchronized static ILogFactory getDefault() {

		if (SINGLETON == null) {
			SINGLETON = new DefaultLogFactory();
		}

		return SINGLETON;

	}

	private static ILogFactory SINGLETON;

	private static enum Level {
		DEBUG, WARN, INFO, ERROR;
	}

	static final class MyLogger implements ILogger {

		private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		private final String name;

		public MyLogger(String name) {
			this.name = name;
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
		}

		public synchronized void core(Level level, String fmt, Throwable t, Object... args) {

			Formatter formatter = new Formatter();

			formatter.format("%5s %s %s: ", level, df.format(new Date()), name);

			if (args == null) {
				try {
					formatter.out().append(fmt);
				} catch (IOException e) {
				}
			} else {
				formatter.format(fmt, args);
			}

			System.out.println(formatter);

			if (t != null) {
				t.printStackTrace(System.out);
			}
		}

		public void debug(Object msg) {
			core(Level.DEBUG, "%s", null, msg);
		}

		public void warn(Object msg) {
			core(Level.WARN, "%s", null, msg);
		}

		public void info(Object msg) {
			core(Level.INFO, "%s", null, msg);
		}

		public void warn(String fmt, Throwable t, Object... args) {
			core(Level.WARN, fmt, t, args);
		}

		public void debugf(String fmt, Object... args) {
			core(Level.DEBUG, fmt, null, args);
		}

		public void infof(String fmt, Object... args) {
			core(Level.INFO, fmt, null, args);
		}

		public void warnf(String fmt, Object... args) {
			core(Level.WARN, fmt, null, args);
		}

		public void errorf(String fmt, Object... args) {
			core(Level.ERROR, fmt, null, args);
		}
	}

}