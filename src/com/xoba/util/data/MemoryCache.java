package com.xoba.util.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.WeakHashMap;

public class MemoryCache implements ICache {

	private final Map<String, IRecord> objects;

	private static interface IRecord {

		public Object getObject();

		public Date getDate();
	}

	public void setDebuggingOutput(boolean debug) {
	}

	public MemoryCache(boolean weak) {
		if (weak) {
			objects = new WeakHashMap<String, IRecord>();
		} else {
			objects = new HashMap<String, IRecord>();
		}
	}

	private static IRecord createRecord(final Object o) {
		final Date d = new Date();
		return new IRecord() {

			public Date getDate() {
				return d;
			}

			public Object getObject() {
				return o;
			}
		};
	}

	public synchronized Date getDateObjectStored(String key) {
		IRecord rec = objects.get(key);
		return rec == null ? null : rec.getDate();
	}

	public synchronized long getStoredSizeEstimate(String key) {
		return 0;
	}

	public synchronized Object getObject(String key) {
		return objects.get(key).getObject();
	}

	public synchronized boolean hasObjectForKey(String key) {
		return objects.containsKey(key);
	}

	public synchronized void storeObject(String key, Object o) {
		objects.put(key, createRecord(o));
	}

	public synchronized boolean removeObject(String key) {
		objects.remove(key);
		return true;
	}

	public synchronized Iterator<String> iterator() {
		return new TreeSet<String>(objects.keySet()).iterator();
	}

}
