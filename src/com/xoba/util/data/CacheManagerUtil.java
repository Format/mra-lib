package com.xoba.util.data;

import java.util.Date;

public class CacheManagerUtil {

	public static <T> ICacheManager<T> createDailyCacheManager() {
		return new ICacheManager<T>() {
			public boolean canUsePreviouslyCachedData(com.xoba.util.data.ICacheManager.IDataBrowser<T> browser,
					Date cacheCreationDate, long size) {
				long now = System.currentTimeMillis();
				long cached = cacheCreationDate.getTime();
				return now - cached < 12L * 3600L * 1000L;
			}
		};
	}

	/**
	 * returns a cache manager based solely on ID, yet invalidates after the
	 * given number of days
	 * 
	 * @param <T>
	 * @param daysExpiration
	 * @return
	 */
	public static <T> ICacheManager<T> createIDBasedCacheManager(final long daysExpiration) {
		return new ICacheManager<T>() {
			public boolean canUsePreviouslyCachedData(com.xoba.util.data.ICacheManager.IDataBrowser<T> browser,
					Date cacheCreationDate, long size) {
				long today = System.currentTimeMillis();
				long created = cacheCreationDate.getTime();
				return (today - created) < (daysExpiration * 24L * 3600L * 1000L);
			}
		};
	}

	public static <T> ICacheManager<T> createMillisExpirationCacheManager(final long expiration) {
		return new ICacheManager<T>() {
			public boolean canUsePreviouslyCachedData(com.xoba.util.data.ICacheManager.IDataBrowser<T> browser,
					Date cacheCreationDate, long size) {
				return System.currentTimeMillis() - cacheCreationDate.getTime() < expiration;
			}
		};
	}
}
