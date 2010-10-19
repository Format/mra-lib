package com.xoba.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class LogFactory {

	public synchronized static ILogFactory getDefault() {

		if (SINGLETON == null) {
			SINGLETON = new DefaultLogFactory();
		}

		return SINGLETON;

	}

	private static ILogFactory SINGLETON;

	static final class MyLogger implements ILogger {

		private final DateFormat df = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		private final String name;

		public MyLogger(String name) {
			this.name = name;
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
		}

		public void debugf(String fmt, Object... args) {
			core("DEBUG", fmt, null, args);
		}

		public synchronized void core(String type, String fmt, Throwable t,
				Object... args) {
			System.out.printf("%5s %s %s: ", type, df.format(new Date()), name);
			if (args == null) {
				System.out.print(fmt);
			} else {
				System.out.printf(fmt, args);
			}
			System.out.println();
			if (t != null) {
				t.printStackTrace(System.out);
			}
		}

		public void warn(String fmt, Throwable t, Object... args) {
			core("WARN", fmt, t, args);

		}

		public void warnf(String fmt, Object... args) {
			core("WARN", fmt, null, args);
		}

		public void errorf(String fmt, Object... args) {
			core("ERROR", fmt, null, args);
		}
	}

}