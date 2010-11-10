package com.xoba.util.data;

import java.util.Date;

public interface ICacheManager<T> {

	public static interface IDataBrowser<T> {

		/**
		 * whether data is currently loaded in memory, read to be browsed
		 * 
		 * @return
		 */
		public boolean isDataAlreadyInMemory();

		/**
		 * get access to the data for whatever purposes necessary
		 * 
		 * @return
		 * @throws Exception
		 */
		public T browseData() throws Exception;

	}

	public boolean canUsePreviouslyCachedData(IDataBrowser<T> browser, Date cacheCreationDate, long size);

}
