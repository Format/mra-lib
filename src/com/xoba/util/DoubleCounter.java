package com.xoba.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DoubleCounter<T> implements Iterable<Map.Entry<T, Double>> {

	@Override
	public String toString() {
		return map.toString();
	}

	private Map<T, Double> map = new HashMap<T, Double>();

	private boolean immutable = false;

	public void inc(T key) {
		inc(key, 1.0);
	}

	public void inc(T key, double x) {
		if (immutable) {
			throw new IllegalStateException("immutable");
		}
		if (map.containsKey(key)) {
			map.put(key, map.get(key) + x);
		} else {
			map.put(key, x);
		}
	}

	public void setImmutable() {
		this.immutable = true;
	}

	public double get(T key) {
		Double out = map.get(key);
		if (out == null) {
			return 0;
		} else {
			return out;
		}
	}

	public Set<T> keySet() {
		return Collections.unmodifiableMap(map).keySet();
	}

	public Collection<Double> values() {
		return Collections.unmodifiableMap(map).values();
	}

	public Map<T, Double> getSorted(boolean ascending) {
		return MraUtils.sortByComparableValues(map, ascending);
	}

	public Iterator<Map.Entry<T, Double>> iterator() {
		return Collections.unmodifiableMap(map).entrySet().iterator();
	}

	public int size() {
		return map.size();
	}

	public double sum() {
		double out = 0;
		for (Double x : map.values()) {
			out += x;
		}
		return out;
	}
}