package com.xoba.mra;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

public class DatabaseUtils {

	private static final ILogger logger = LogFactory.getDefault().create();

	public static void dumpCSVToPrintWriter(ICSVProvider csv, PrintWriter pw, char sep, String naValue) {

		logger.debugf("dumping csv: %s", csv.getColumnNames());

		SortedMap<Integer, String> header = csv.getColumnNames();

		{
			Iterator<String> it = header.values().iterator();
			while (it.hasNext()) {
				pw.print(it.next());
				if (it.hasNext()) {
					pw.print(sep);
				}
			}
			pw.println();
		}

		for (SortedMap<Integer, Object> row : csv) {

			Iterator<Integer> it = header.keySet().iterator();
			while (it.hasNext()) {

				Integer i = it.next();

				if (row.containsKey(i)) {

					Object cell = row.get(i);

					if (cell == null) {
						pw.print(naValue);
					} else {
						if (cell instanceof Number) {

							Number n = (Number) cell;

							if (n instanceof Double) {
								Double v = (Double) n;
								if (Double.isNaN(v) || Double.isInfinite(v)) {
									pw.print(naValue);
								} else {
									pw.print(v);
								}
							} else if (n instanceof Float) {
								Float v = (Float) n;
								if (Float.isNaN(v) || Float.isInfinite(v)) {
									pw.print(naValue);
								} else {
									pw.print(v);
								}
							} else {
								pw.print(n);
							}
						} else {
							String s = cell.toString();
							if (s.length() == 0) {
								pw.print(naValue);
							} else {
								pw.print(s);
							}
						}
					}
				} else {
					pw.print(naValue);
				}

				if (it.hasNext()) {
					pw.print(sep);
				}
			}
			pw.println();
		}

	}

	public static ICSVProvider runQuery(DataSource ds, String query) throws Exception {
		Connection c = ds.getConnection();
		try {
			return runQuery(c, query);
		} finally {
			c.close();
		}
	}

	public static ICSVProvider runQuery(Connection c, String query) throws Exception {
		logger.debugf("running query: %s", query);
		Statement s = c.createStatement();
		try {
			ResultSet r = s.executeQuery(query);
			try {
				return runQuery(r);
			} finally {
				r.close();
			}
		} finally {
			s.close();
		}
	}

	public static ICSVProvider runQuery(ResultSet r) throws Exception {
		ResultSetMetaData md = r.getMetaData();
		int n = md.getColumnCount();

		final SortedMap<Integer, String> labels = new TreeMap<Integer, String>();

		for (int i = 0; i < n; i++) {
			labels.put(i, md.getColumnName(i + 1));
		}

		final List<SortedMap<Integer, Object>> rows = new LinkedList<SortedMap<Integer, Object>>();

		while (r.next()) {
			SortedMap<Integer, Object> map = new TreeMap<Integer, Object>();
			for (int i = 0; i < n; i++) {
				switch (md.getColumnType(i + 1)) {
				case Types.DOUBLE:
					map.put(i, r.getDouble(i + 1));
					break;
				default:
					map.put(i, r.getObject(i + 1));
					break;
				}
			}
			rows.add(Collections.unmodifiableSortedMap(map));
		}

		return new AbstractCSVProvider() {

			@Override
			public Iterator<SortedMap<Integer, Object>> iterator() {
				return Collections.unmodifiableList(rows).iterator();
			}

			@Override
			public SortedMap<Integer, String> getColumnNames() {
				return Collections.unmodifiableSortedMap(labels);
			}

		};

	}

	public static byte[] dumpCSVToPlainBytes(ICSVProvider csv, String naValue) {

		logger.debugf("dumping csv: %s", csv.getColumnNames());

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		PrintWriter pw = new PrintWriter(out);

		SortedMap<Integer, String> header = csv.getColumnNames();

		{
			Iterator<String> it = header.values().iterator();
			while (it.hasNext()) {
				pw.print(it.next());
				if (it.hasNext()) {
					pw.print(",");
				}
			}
			pw.println();
		}

		for (SortedMap<Integer, Object> row : csv) {

			Iterator<Integer> it = header.keySet().iterator();
			while (it.hasNext()) {

				Integer i = it.next();

				if (row.containsKey(i)) {

					Object cell = row.get(i);

					if (cell == null) {
						pw.print(naValue);
					} else {
						if (cell instanceof Number) {

							Number n = (Number) cell;

							if (n instanceof Double) {
								Double v = (Double) n;
								if (Double.isNaN(v) || Double.isInfinite(v)) {
									pw.print(naValue);
								} else {
									pw.print(v);
								}
							} else if (n instanceof Float) {
								Float v = (Float) n;
								if (Float.isNaN(v) || Float.isInfinite(v)) {
									pw.print(naValue);
								} else {
									pw.print(v);
								}
							} else {
								pw.print(n);
							}
						} else {
							String s = cell.toString();
							if (s.length() == 0) {
								pw.print(naValue);
							} else {
								pw.print(s);
							}
						}
					}
				} else {
					pw.print(naValue);
				}

				if (it.hasNext()) {
					pw.print(",");
				}
			}
			pw.println();
		}

		pw.close();

		return out.toByteArray();
	}

	public static String dumpCSV(ICSVProvider csv, long maxRows) throws Exception {
		return dumpCSV(csv, maxRows, 11, false, null);
	}

	public static String dumpCSV(ICSVProvider csv) throws Exception {
		return dumpCSV(csv, Long.MAX_VALUE, 11, false, null);
	}

	private static String esc(Object x) {
		if (x == null) {
			return "null";
		}
		String s = x.toString();
		StringBuffer buf = new StringBuffer();
		for (char c : s.toCharArray()) {
			switch (c) {
			case '\t':
				buf.append("   ");
				break;
			case '\n':
			case '\r':
				buf.append(" ");
				break;
			default:
				if (Character.isISOControl(c)) {
					buf.append("#");
				} else {
					buf.append(c);
				}
			}
		}
		return buf.toString();
	}

	public static ICSVProvider getNullCSV() {
		List<Map<String, Object>> rows = new LinkedList<Map<String, Object>>();
		rows.add(Collections.singletonMap("bogus", null));
		return new WrapperCSV(rows);
	}

	public static String rawDump(ICSVProvider csv, String delim, String missingValueString) throws Exception {

		if (csv == null) {
			csv = getNullCSV();
		}

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		{
			SortedMap<Integer, String> headers = csv.getColumnNames();
			Iterator<String> it = headers.values().iterator();
			while (it.hasNext()) {
				pw.print(it.next());
				if (it.hasNext()) {
					pw.print(delim);
				}
			}
			pw.println();
		}
		for (SortedMap<Integer, Object> row : csv) {
			Iterator<Object> it = row.values().iterator();
			while (it.hasNext()) {
				Object o = it.next();
				if (o == null) {
					pw.print(missingValueString);
				} else {
					if (o instanceof Double) {
						Double x = (Double) o;
						if (Double.isNaN(x)) {
							pw.print(missingValueString);
						} else {
							pw.print(x);
						}
					} else if (o instanceof Float) {
						Float x = (Float) o;
						if (Float.isNaN(x)) {
							pw.print(missingValueString);
						} else {
							pw.print(x);
						}
					} else {
						pw.print(o);
					}
				}
				if (it.hasNext()) {
					pw.print(delim);
				}
			}
			pw.println();
		}
		pw.close();
		return sw.toString();
	}

	/**
	 * if html, encloses highlighted columns in "<span class='hl'></span>"
	 * 
	 * @param csv
	 * @param maxRows
	 * @param minWidth
	 * @param html
	 * @param highlighed
	 * @return
	 * @throws Exception
	 */
	public static String dumpCSV(ICSVProvider csv, long maxRows, int minWidth, boolean html, Set<String> highlighed)
			throws Exception {

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));

		boolean reachedMax = false;

		if (csv == null) {
			csv = getNullCSV();
		}

		if (highlighed == null) {
			highlighed = new HashSet<String>();
		}

		SortedMap<Integer, Integer> lengths = new TreeMap<Integer, Integer>();

		List<SortedMap<Integer, String>> stringRows = new LinkedList<SortedMap<Integer, String>>();

		int rowCount = 0;
		{

			for (SortedMap<Integer, Object> row : csv) {

				if (rowCount++ < maxRows) {
					SortedMap<Integer, String> out = new TreeMap<Integer, String>();

					for (Integer i : row.keySet()) {
						Object o = row.get(i);

						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);

						if (o != null) {
							if (o instanceof Number) {
								if (o instanceof Double) {
									Double d = (Double) o;
									if (!Double.isNaN(d)) {
										pw.format("%g", d);
									}
								} else if (o instanceof Float) {
									Float f = (Float) o;
									if (!Float.isNaN(f)) {
										pw.format("%g", f);
									}
								} else if (o instanceof BigDecimal) {
									pw.format("%g", o);
								} else if (o instanceof AtomicInteger) {
									pw.format("%d", ((AtomicInteger) o).intValue());
								} else if (o instanceof AtomicLong) {
									pw.format("%d", ((AtomicLong) o).longValue());
								} else {
									pw.format("%d", o);
								}
							} else if (o instanceof Date) {
								pw.print(df.format((Date) o));
							} else {
								pw.print(esc(o));
							}
						}

						pw.close();

						out.put(i, sw.toString());
					}

					stringRows.add(out);
				} else {
					reachedMax = true;
				}
			}
		}

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		if (reachedMax) {
			pw.printf("WARNING: rows %,d to %,d not shown below", maxRows + 1, rowCount);
			pw.println();
		}

		SortedMap<Integer, String> labels = csv.getColumnNames();

		if (labels.size() > 0) {

			Set<Integer> hl = new HashSet<Integer>();

			for (Integer i : labels.keySet()) {
				if (!lengths.containsKey(i)) {
					lengths.put(i, minWidth);
				}
				String s = labels.get(i);
				if (highlighed.contains(s)) {
					hl.add(i);
				}
				if (s.length() > lengths.get(i)) {
					lengths.put(i, s.length());
				}
			}

			int count = 0;
			for (SortedMap<Integer, String> row : stringRows) {
				if (count++ < maxRows) {
					for (Integer i : row.keySet()) {
						if (!lengths.containsKey(i)) {
							lengths.put(i, minWidth);
						}
						String o = row.get(i);
						int len = o.length();
						if (len > lengths.get(i)) {
							lengths.put(i, len);
						}
					}
				}
			}

			String horizontalLine = null;

			{
				StringWriter sw3 = new StringWriter();
				PrintWriter pw3 = new PrintWriter(sw3);

				pw3.print("+");
				for (int x : lengths.values()) {
					for (int i = 0; i < x; i++) {
						pw3.print("-");
					}
					pw3.print("--+");
				}

				pw3.close();

				horizontalLine = sw3.toString();

			}

			pw.println(horizontalLine);

			{
				Iterator<Integer> it = labels.keySet().iterator();
				while (it.hasNext()) {
					Integer i = it.next();
					String s = labels.get(i);
					if (html && hl.contains(i)) {
						pw.printf("| <span class='hl'>%-" + lengths.get(i) + "s</span> ", s);
					} else {
						pw.printf("| %-" + lengths.get(i) + "s ", s);
					}
				}
				pw.println("|");
			}

			pw.println(horizontalLine);

			count = 0;
			for (SortedMap<Integer, String> row : stringRows) {
				if (count++ < maxRows) {
					Iterator<Integer> it = labels.keySet().iterator();
					while (it.hasNext()) {
						Integer i = it.next();
						if (row.containsKey(i)) {
							String s = row.get(i);
							if (html && hl.contains(i)) {
								pw.printf("| <span class='hl'>%-" + lengths.get(i) + "s</span> ", s == null ? "" : s);
							} else {
								pw.printf("| %-" + lengths.get(i) + "s ", s == null ? "" : s);
							}
						} else {
							if (html && hl.contains(i)) {
								pw.printf("| <span class='hl'>%-" + lengths.get(i) + "s</span> ", "");
							} else {
								pw.printf("| %-" + lengths.get(i) + "s ", "");
							}
						}
					}
					pw.println("|");
				}
			}

			pw.println(horizontalLine);
		}

		pw.close();
		return sw.toString();
	}

}
