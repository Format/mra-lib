package com.xoba.util.data;

public abstract class AbstractManagedData<T> implements IManagedData<T> {

	private boolean useDailyManager;

	public static enum Type {

		DAILY_RELOADING_CACHE_MANAGER,

		ID_BASED_CACHE_MANAGER;

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
