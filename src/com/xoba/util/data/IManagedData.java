package com.xoba.util.data;

import java.io.Serializable;

public interface IManagedData<T> extends Serializable {

	public T createData() throws Exception;

	/**
	 * unique ID for the data
	 * 
	 * @return
	 */
	public String getID();

	public String getDescription();

	/**
	 * decides how to cache the data
	 * 
	 * @return
	 */
	public ICacheManager<T> getCacheManager();

}