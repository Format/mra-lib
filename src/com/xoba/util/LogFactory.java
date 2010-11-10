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

	static final class MyLogger implements ILogger {

		private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		private final String name;

		public MyLogger(String name) {
			this.name = name;
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
		}

		public void debugf(String fmt, Object... args) {
			core("DEBUG", fmt, null, args);
		}

		public synchronized void core(String type, String fmt, Throwable t, Object... args) {

			Formatter formatter = new Formatter();

			formatter.format("%5s %s %s: ", type, df.format(new Date()), name);

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