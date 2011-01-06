package com.xoba.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.TimeZone;

public class TimeUtils {

	private static DateFormat D8 = new SimpleDateFormat("yyyyMMdd");
	private static DateFormat D9 = new SimpleDateFormat("yyyyMMdd'Z'");
	private static DateFormat D10 = new SimpleDateFormat("yyyy-MM-dd");
	private static DateFormat D11 = new SimpleDateFormat("yyyy-MM-dd'Z'");
	private static DateFormat D15 = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
	private static DateFormat D16 = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");

	private static DateFormat D19a = new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSS");
	private static DateFormat D19b = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	private static DateFormat D20a = new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSS'Z'");
	private static DateFormat D20b = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	private static DateFormat D23 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	private static DateFormat D24 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	private static DateFormat D25 = new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSS");

	private static DateFormat D26 = new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSS'Z'");

	private static DateFormat D29 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	private static DateFormat D30 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	private static final DateFormat US_EASTERN_FORMAT_SECONDS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static DateFormat US_EASTERN_FORMAT_MILLIS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	private static DateFormat GMT_FORMAT_YEAR_NO_Z = new SimpleDateFormat("yyyy");
	private static DateFormat GMT_FORMAT_MILLIS_NO_Z = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	private static DateFormat GMT_FORMAT_MILLIS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	public static String getTimestamp() {
		synchronized (D16) {
			return D16.format(new Date());
		}
	}

	static {
		TimeZone nyc = TimeZone.getTimeZone("US/Eastern");
		TimeZone gmt = TimeZone.getTimeZone("GMT");

		D8.setTimeZone(nyc);
		D10.setTimeZone(nyc);
		D15.setTimeZone(nyc);
		D19a.setTimeZone(nyc);
		D19b.setTimeZone(nyc);
		D23.setTimeZone(nyc);
		D25.setTimeZone(nyc);
		D29.setTimeZone(nyc);

		D9.setTimeZone(gmt);
		D11.setTimeZone(gmt);
		D16.setTimeZone(gmt);
		D20a.setTimeZone(gmt);
		D20b.setTimeZone(gmt);
		D24.setTimeZone(gmt);
		D26.setTimeZone(gmt);
		D30.setTimeZone(gmt);

		US_EASTERN_FORMAT_MILLIS.setTimeZone(nyc);
		US_EASTERN_FORMAT_SECONDS.setTimeZone(nyc);

		GMT_FORMAT_MILLIS.setTimeZone(gmt);
		GMT_FORMAT_MILLIS_NO_Z.setTimeZone(gmt);
		GMT_FORMAT_YEAR_NO_Z.setTimeZone(gmt);

	}

	private static final long MM = 1000000L;

	public static final long parseNanos(final String date) throws Exception {

		switch (date.length()) {

		case 8: { // '0' 20100824
			synchronized (D8) {
				return MM * D8.parse(date).getTime();
			}
		}

		case 9: { // '0' 20100824Z
			synchronized (D9) {
				return MM * D9.parse(date).getTime();
			}
		}

		case 10: { // '-' 2010-08-24
			synchronized (D10) {
				return MM * D10.parse(date).getTime();
			}
		}

		case 11: { // '-' 2010-08-24Z
			synchronized (D11) {
				return MM * D11.parse(date).getTime();
			}
		}

		case 15: { // '0' 20100824T120000
			synchronized (D15) {
				return MM * D15.parse(date).getTime();
			}
		}

		case 16: { // '0' 20100824T120000Z
			synchronized (D16) {
				return MM * D16.parse(date).getTime();
			}
		}

		case 19: { // '0' 20100824T120000.000 // '-' 2010-08-24T12:00:00
			switch (date.charAt(4)) {
			case '-':
				synchronized (D19b) {
					return MM * D19b.parse(date).getTime();
				}
			default:
				synchronized (D19a) {
					return MM * D19a.parse(date).getTime();
				}
			}
		}

		case 20: { // '0' 20100824T120000.000Z // '-' 2010-08-24T12:00:00Z
			switch (date.charAt(4)) {
			case '-':
				synchronized (D20b) {
					return MM * D20b.parse(date).getTime();
				}
			default:
				synchronized (D20a) {
					return MM * D20a.parse(date).getTime();
				}
			}
		}

		case 23: { // '-' 2010-08-24T12:00:00.000
			synchronized (D23) {
				return MM * D23.parse(date).getTime();
			}
		}
		case 24: { // '-' 2010-08-24T12:00:00.000Z
			synchronized (D24) {
				return MM * D24.parse(date).getTime();
			}
		}

		case 25: { // '0' 20100824T120000.000000000
			final String time = date.substring(0, 19);
			final long nanos = new Long(date.substring(19));
			synchronized (D25) {
				return MM * D25.parse(time).getTime() + nanos;
			}
		}

		case 26: { // '0' 20100824T120000.000000000Z
			final String time = date.substring(0, 19) + "Z";
			final long nanos = new Long(date.substring(19, 25));
			synchronized (D26) {
				return MM * D26.parse(time).getTime() + nanos;
			}
		}

		case 29: { // '-' 2010-08-24T12:00:00.000000000
			final String time = date.substring(0, 23);
			final long nanos = new Long(date.substring(23));
			synchronized (D29) {
				return MM * D29.parse(time).getTime() + nanos;
			}
		}

		case 30: { // '-' 2010-08-24T12:00:00.000000000Z
			final String time = date.substring(0, 23) + "Z";
			final long nanos = new Long(date.substring(23, 29));
			synchronized (D30) {
				return MM * D30.parse(time).getTime() + nanos;
			}
		}

		}
		throw new IllegalArgumentException(date);
	}

	public static long getNanosNewYorkTime(String date, String time) throws Exception {
		return parseNanos(date + "T" + time);
	}

	public static long getNanosUSEasternMarketPreOpen(String date) throws Exception {
		return getNanosNewYorkTime(date, "09:00:00");
	}

	public static long getNanosUSEasternMarketPostClose(String date) throws Exception {
		return getNanosNewYorkTime(date, "17:00:00");
	}

	public static long getNanosUSEasternEndOfDay(String date) throws Exception {
		if (date.contains("-")) {
			return getNanosNewYorkTime(date, "23:59:59");
		} else {
			return getNanosNewYorkTime(date, "235959");
		}
	}

	public static long getNanosUSEasternStartOfDay(String date) throws Exception {
		return parseNanos(date);
	}

	public static long parseGMTInMillis(String time) throws ParseException {
		synchronized (GMT_FORMAT_MILLIS) {
			return GMT_FORMAT_MILLIS.parse(time).getTime() * 1000000L;
		}
	}

	public static String formatNanosYearGMT(long nanos) {
		long millis = nanos / 1000000;
		Date d = new Date(millis);
		synchronized (GMT_FORMAT_MILLIS_NO_Z) {
			return GMT_FORMAT_YEAR_NO_Z.format(d);
		}
	}

	public static String formatNanosTimeGMT(long nanos) {
		long millis = nanos / 1000000;
		Formatter nanosFormat = new Formatter();
		nanosFormat.format("%06dZ", nanos - millis * 1000000);
		Date d = new Date(millis);
		synchronized (GMT_FORMAT_MILLIS_NO_Z) {
			return GMT_FORMAT_MILLIS_NO_Z.format(d) + nanosFormat.toString();
		}
	}

	public static String formatNanosTimeUSEastern(long nanos) {
		long millis = nanos / 1000000;
		Formatter nanosFormat = new Formatter();
		nanosFormat.format("%06d", nanos - millis * 1000000);
		Date d = new Date(millis);
		synchronized (US_EASTERN_FORMAT_MILLIS) {
			return US_EASTERN_FORMAT_MILLIS.format(d) + nanosFormat.toString();
		}
	}

	public static final long NANOS_IN_DAY = 24L * 3600L * 1000L * 1000000L;

}
