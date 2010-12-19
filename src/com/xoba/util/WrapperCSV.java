package com.xoba.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class WrapperCSV extends AbstractCSVProvider {

	private final List<SortedMap<Integer, Object>> rowsByInteger = new LinkedList<SortedMap<Integer, Object>>();

	private final SortedMap<Integer, String> header = new TreeMap<Integer, String>();

	public WrapperCSV(List<Map<String, Object>> rowsByString) {
		Set<String> cols = new LinkedHashSet<String>();
		for (Map<String, ? extends Object> row : rowsByString) {
			cols.addAll(row.keySet());
		}

		Map<String, Integer> indicies = new HashMap<String, Integer>();
		for (String s : cols) {
			int n = header.size();
			header.put(n, s);
			indicies.put(s, n);
		}
		for (Map<String, ? extends Object> row : rowsByString) {
			SortedMap<Integer, Object> out = new TreeMap<Integer, Object>();
			for (String s : row.keySet()) {
				out.put(indicies.get(s), row.get(s));
			}
			rowsByInteger.add(Collections.unmodifiableSortedMap(out));
		}

	}

	public SortedMap<Integer, String> getColumnNames() {
		return Collections.unmodifiableSortedMap(header);
	}

	public Iterator<SortedMap<Integer, Object>> iterator() {
		return Collections.unmodifiableList(rowsByInteger).iterator();
	}

}