package com.xoba.util.data;

import java.util.Date;

/**
 * cache object, iterable over its keys
 * 
 */
public interface ICache extends Iterable<String> {

	public boolean hasObjectForKey(String key);

	public Object getObject(String key) throws Exception;

	public Date getDateObjectStored(String key);

	public long getStoredSizeEstimate(String key);

	public void storeObject(String key, Object o) throws Exception;

	public boolean removeObject(String key);

}
