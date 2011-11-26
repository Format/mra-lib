package com.xoba.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class LongCounter<T> implements Iterable<Map.Entry<T, Long>>, Serializable {

	@Override
	public int hashCode() {
		return map.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LongCounter) {
			LongCounter<T> other = (LongCounter<T>) obj;
			return map.equals(other.map);
		} else {
			return false;
		}
	}

	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return map.toString();
	}

	private final Map<T, Long> map = new HashMap<T, Long>();

	private boolean immutable = false;

	public void accumulate(LongCounter<T> o) {
		for (T key : o.keySet()) {
			inc(key, o.get(key));
		}
	}

	public void inc(T key) {
		inc(key, 1);
	}

	public void inc(T key, long x) {
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

	public long get(T key) {
		Long out = map.get(key);
		if (out == null) {
			return 0;
		} else {
			return out;
		}
	}

	public Set<T> keySet() {
		return Collections.unmodifiableMap(map).keySet();
	}

	public Collection<Long> values() {
		return Collections.unmodifiableMap(map).values();
	}

	public Map<T, Long> getSorted(boolean ascending) {
		return MraUtils.sortByComparableValues(map, ascending);
	}

	public Map<T, Long> getMap() {
		return new HashMap<T, Long>(map);
	}

	@Override
	public Iterator<Map.Entry<T, Long>> iterator() {
		return Collections.unmodifiableMap(map).entrySet().iterator();
	}

	public int size() {
		return map.size();
	}

	public long sum() {
		long out = 0;
		for (Long x : map.values()) {
			out += x;
		}
		return out;
	}
}