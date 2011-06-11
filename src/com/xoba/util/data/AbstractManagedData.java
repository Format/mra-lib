package com.xoba.util.data;

import java.io.File;
import java.util.Date;

import com.xoba.util.ILogger;
import com.xoba.util.LogFactory;

public abstract class AbstractManagedData<T> implements IManagedData<T> {

	private static final ILogger logger = LogFactory.getDefault().create();

	private boolean useDailyManager;

	public static enum Type {

		DAILY_RELOADING_CACHE_MANAGER,

		ID_BASED_CACHE_MANAGER;

	}

	public static void main(String... args) throws Exception {

		IManagedData<String> testData = new AbstractManagedData<String>() {

			public String createData() throws Exception {
				return "test on " + new Date();
			}

			public String getID() {
				return "test123";
			}
		};

		IObjectManager om = new ObjectManager(false, new File("/tmp/om"));

		String test = om.evaluateData(testData);
		logger.debugf("got %s", test);

	}

	public AbstractManagedData() {
		this(Type.ID_BASED_CACHE_MANAGER);
	}

	public AbstractManagedData(Type manager) {
		this.useDailyManager = (manager == Type.DAILY_RELOADING_CACHE_MANAGER);
	}

	public static final int DAYS_EXPIRATION_FOR_ID_BASED_MANAGER = 5;

	public ICacheManager<T> getCacheManager() {

		ICacheManager<T> a = null;

		if (useDailyManager) {
			a = CacheManagerUtil.createDailyCacheManager();
		} else {
			a = CacheManagerUtil.createIDBasedCacheManager(DAYS_EXPIRATION_FOR_ID_BASED_MANAGER);
		}

		return a;
	}

	public String getDescription() {
		return getID();
	}

}
