package com.xoba.mra;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractCSVProvider implements ICSVProvider {

	public Set<Integer> getIndexForColumnName(String name) {
		Set<Integer> out = new HashSet<Integer>();
		for (Map.Entry<Integer, String> e : getColumnNames().entrySet()) {
			if (e.getValue().equals(name)) {
				out.add(e.getKey());
			}
		}
		return out;
	}

}