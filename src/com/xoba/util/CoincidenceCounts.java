package com.xoba.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CoincidenceCounts<T> {

	private final Map<T, Map<T, Long>> counts = new HashMap<T, Map<T, Long>>();

	private long updates;

	public void inc(Set<T> set) {
		updates++;
		for (T x : set) {
			if (!counts.containsKey(x)) {
				counts.put(x, new HashMap<T, Long>());
			}
			Map<T, Long> map = counts.get(x);
			for (T y : set) {
				if (map.containsKey(y)) {
					map.put(y, map.get(y) + 1L);
				} else {
					map.put(y, 1L);
				}
			}
		}
	}

	public long getTotalUpdates() {
		return updates;
	}

	public List<T> getTypes() {
		return new LinkedList<T>(counts.keySet());
	}

	public long get(T x, T y) {
		if (counts.containsKey(x)) {
			Map<T, Long> map = counts.get(x);
			if (map.containsKey(y)) {
				return map.get(y);
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}
}
