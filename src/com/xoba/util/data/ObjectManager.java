package com.xoba.util.data;

import java.io.File;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import com.xoba.util.ILogger;
import com.xoba.util.LogFactory;
import com.xoba.util.data.ICacheManager.IDataBrowser;

/**
 * second-generation object manager
 * 
 * handles changes in serialization format gracefully, retries, etc...
 * 
 */
public class ObjectManager implements IObjectManager {

	private static final ILogger logger = LogFactory.getDefault().create();

	public ObjectManager(boolean compress, boolean weak, File storageDir) {
		firstLevelCache = new MemoryCache(weak);
		secondLevelCache = storageDir == null ? new NullCache() : new FileCache(storageDir, compress);
	}

	public ObjectManager(boolean compress, File storageDir) {
		firstLevelCache = new NullCache();
		secondLevelCache = storageDir == null ? new NullCache() : new FileCache(storageDir, compress);
	}

	private final ICache firstLevelCache, secondLevelCache;

	private final int retries = 3;

	private static class NullCache implements ICache {

		public Date getDateObjectStored(String key) {
			return null;
		}

		public Object getObject(String key) throws Exception {
			return null;
		}

		public long getStoredSizeEstimate(String key) {
			return 0;
		}

		public boolean hasObjectForKey(String key) {
			return false;
		}

		public boolean removeObject(String key) {
			return true;
		}

		public void storeObject(String key, Object o) throws Exception {
		}

		public Iterator<String> iterator() {
			return null;
		}

	}

	private static <T> IDataBrowser<T> getInMemoryDataBrowser(final T data) {
		return new ICacheManager.IDataBrowser<T>() {

			public T browseData() throws Exception {
				return data;
			}

			public boolean isDataAlreadyInMemory() {
				return true;
			}
		};
	}

	private static <T> IDataBrowser<T> getOnDemandDataBrowser(final ICache cache, final String key) {

		return new ICacheManager.IDataBrowser<T>() {

			private T data;

			@SuppressWarnings("unchecked")
			public synchronized T browseData() throws Exception {
				if (data == null) {
					data = (T) cache.getObject(key);
				}
				return data;
			}

			public synchronized boolean isDataAlreadyInMemory() {
				return data != null;
			}
		};
	}

	@SuppressWarnings({ "unused", "unchecked" })
	private static <T> T getObjectFromCache(Map<Object, Object> cache, Object key) {
		return (T) cache.get(key);
	}

	private static <T> T createAndStoreData(IManagedData<T> data, ICache[] caches) throws Exception {
		T newObject = data.createData();
		return storeData(newObject, data.getID(), caches);
	}

	private static <T> T storeData(T newObject, String key, ICache[] caches) throws Exception {
		for (ICache c : caches) {
			c.storeObject(key, newObject);
		}
		return newObject;
	}

	private IDLockManager lm = new IDLockManager();

	public <T> T evaluateData(IManagedData<T> managedData) throws Exception {
		boolean done = false;
		int tries = 0;
		T data = null;
		while (!done && tries < retries) {
			tries++;
			Throwable t = null;
			try {
				data = evaluateDataOnce(managedData);
				done = true;
			} catch (InvalidClassException e) {
				t = e;
			} catch (NotSerializableException e) {
				done = true;
				t = e;
			} catch (RuntimeException e) {
				t = e;
			} catch (Exception e) {
				t = e;
			}
			if (t != null) {
				boolean removed = removeDataFromManagement(managedData.getID());
				logger.warn("exception getting " + managedData.getID() + " on try " + tries, t);

				if (!removed) {
					throw new RuntimeException("couldn't remove " + managedData.getID());
				}
			}
		}
		if (data == null) {
			throw new Exception("couldn't get data " + managedData.getID());
		} else {
			return data;
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T evaluateDataOnce(IManagedData<T> managedData) throws Exception {

		lm.lockID(managedData.getID());

		try {
			ICache[] allCaches = new ICache[] { firstLevelCache, secondLevelCache };
			ICache[] memoryCachesOnly = new ICache[] { firstLevelCache };

			String key = managedData.getID();

			if (firstLevelCache.hasObjectForKey(key)) {
				// object is already in memory

				T cachedObject = (T) firstLevelCache.getObject(key);
				IDataBrowser<T> browser = getInMemoryDataBrowser(cachedObject);

				boolean canUseCached = managedData.getCacheManager().canUsePreviouslyCachedData(browser,
						firstLevelCache.getDateObjectStored(key), firstLevelCache.getStoredSizeEstimate(key));

				if (canUseCached) {
					return cachedObject;
				} else {
					return createAndStoreData(managedData, allCaches);
				}

			} else if (secondLevelCache.hasObjectForKey(managedData.getID())) {
				// object is not in memory, but possibly in a file

				IDataBrowser<T> browser = getOnDemandDataBrowser(secondLevelCache, key);

				boolean canUseCached = managedData.getCacheManager().canUsePreviouslyCachedData(browser,
						secondLevelCache.getDateObjectStored(key), secondLevelCache.getStoredSizeEstimate(key));

				if (canUseCached) {
					T object = browser.browseData();
					storeData(object, key, memoryCachesOnly);
					return object;
				} else {
					return createAndStoreData(managedData, allCaches);
				}

			} else {
				// object needs to be created and cached from scratch
				logger.debugf("creating object from scratch: %s", managedData.getID());
				return createAndStoreData(managedData, allCaches);
			}
		} finally {
			lm.unlockID(managedData.getID());
		}
	}

	public boolean removeDataFromManagement(String id) {
		logger.debugf("removing from management: %s", id);
		boolean a = firstLevelCache.removeObject(id);
		boolean b = secondLevelCache.removeObject(id);
		return a && b;
	}

}
