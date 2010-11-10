package com.xoba.util.data;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IDLockManager {

	private static final Random r = new Random();

	public static void main(String[] args) {
		ExecutorService es = Executors.newCachedThreadPool();

		final IDLockManager lm = new IDLockManager();

		final String[] ids = new String[] { "a", "b", "c" };
		for (int i = 0; i < 20; i++) {
			final int x = i;
			Runnable task = new Runnable() {
				public void run() {
					int count = 0;
					while (true) {
						count++;
						try {
							String id = ids[r.nextInt(ids.length)];
							lm.lockID(id);
							try {
								Thread.sleep(r.nextInt(300));
							} finally {
								lm.unlockID(id);
								debug("******************* " + x + "/" + count, id);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};
			es.submit(task);
		}

	}

	private Lock lock = new ReentrantLock();

	// we assume there won't be too many simultaneous requests
	private Condition globalCondition = lock.newCondition();

	private Set<String> locked = new HashSet<String>();

	private static final long START = System.currentTimeMillis();

	private static void debug(String message, String id) {
		System.out.println((System.currentTimeMillis() - START) + "; " + Thread.currentThread().getName() + "; " + message + "; " + id);
	}

	/**
	 * tries to lock the given ID, and blocks if its already locked
	 * 
	 * @param id
	 */
	public void lockID(String id) throws InterruptedException {
		lock.lock();
		try {
			while (locked.contains(id)) {
				globalCondition.await();
			}
			locked.add(id);
		} finally {
			lock.unlock();
		}
	}

	public void unlockID(String id) {
		lock.lock();
		try {
			locked.remove(id);
			globalCondition.signalAll();
		} finally {
			lock.unlock();
		}
	}
}
