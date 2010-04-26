package com.xoba.mra;

import java.util.Set;
import java.util.SortedMap;

public interface ICSVProvider extends Iterable<SortedMap<Integer, Object>> {

	public SortedMap<Integer, String> getColumnNames();

	public Set<Integer> getIndexForColumnName(String name);
	
}