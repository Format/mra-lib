package com.xoba.util.data;

public interface IObjectManager {

	/**
	 * evaluates the given data. locks out all other data with same ID
	 * 
	 * @param <T>
	 * @param managedData
	 * @return
	 * @throws Exception
	 */
	public <T> T evaluateData(IManagedData<T> managedData) throws Exception;

	/**
	 * returns whether or not data has been definitively removed from management
	 * 
	 * @param id
	 * @return
	 */
	public boolean removeDataFromManagement(String id);

}