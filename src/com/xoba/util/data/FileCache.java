package com.xoba.util.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.xoba.util.ILogger;
import com.xoba.util.LogFactory;

public class FileCache implements ICache {

	private static final ILogger logger = LogFactory.getDefault().create();

	private final File dir;

	private final boolean compressed;

	public FileCache(File dir, boolean compressed) {
		this.dir = dir;
		dir.mkdirs();
		this.compressed = compressed;
	}

	public FileCache(File dir) {
		this(dir, false);
	}

	private static void putObjectToFile(File file, String objectDescription, Object o, boolean compressed)
			throws Exception {
		logger.debugf("putting object \"" + objectDescription + "\" to " + file);
		ObjectOutputStream out = getOutput(file, compressed);
		try {
			out.writeObject(new Date());
			out.writeObject(objectDescription);
			out.writeObject(o);
		} finally {
			out.close();
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T getObjectFromFile(File file, boolean compressed) throws Exception {
		long start = System.currentTimeMillis();
		ObjectInputStream in = getInput(file, compressed);
		try {
			Date day = (Date) in.readObject();
			String objectDescription = (String) in.readObject();
			Object o = in.readObject();
			return (T) o;
		} catch (ClassNotFoundException e) {
			logger.errorf("incompatible class change, and can't load %s: %s", file, e);
			throw e;
		} catch (InvalidClassException e) {
			logger.errorf("incompatible class change, and can't load %s: %s", file, e);
			throw e;
		} finally {
			in.close();
			long end = System.currentTimeMillis();
		}
	}

	private static ObjectInputStream getInput(File file, boolean compress) throws Exception {
		// need to close the FileInputStream explicitly if there's an
		// IOException, due to weird constructor behaviour of ObjectInputStream:
		// constructor reads from stream and may throw IOException itself!!!
		FileInputStream fin = new FileInputStream(file);
		try {
			if (compress) {
				return new ObjectInputStream(new GZIPInputStream(new BufferedInputStream(fin, 65536)));
			} else {
				return new ObjectInputStream(new BufferedInputStream(fin, 65536));
			}
		} catch (IOException e) {
			fin.close();
			throw e;
		}
	}

	private static ObjectOutputStream getOutput(File file, boolean compress) throws Exception {
		FileOutputStream fout = new FileOutputStream(file);
		try {
			if (compress) {
				return new ObjectOutputStream(new GZIPOutputStream(new BufferedOutputStream(fout, 65536)));
			} else {
				return new ObjectOutputStream(new BufferedOutputStream(fout, 65536));
			}
		} catch (Exception e) {
			fout.close();
			throw e;
		}
	}

	private File getFileForKey(String key) {
		return new File(dir, key);
	}

	public Date getDateObjectStored(String key) {
		return new Date(getFileForKey(key).lastModified());
	}

	public long getStoredSizeEstimate(String key) {
		return getFileForKey(key).length();
	}

	public Object getObject(String key) throws Exception {
		return getObjectFromFile(getFileForKey(key), compressed);
	}

	public boolean hasObjectForKey(String key) {
		return getFileForKey(key).exists();
	}

	public boolean removeObject(String key) {
		return getFileForKey(key).delete();
	}

	public void storeObject(String key, Object o) throws Exception {
		putObjectToFile(getFileForKey(key), "stored by " + FileCache.class, o, compressed);
	}

	public Iterator<String> iterator() {
		String[] children = dir.list();
		return Arrays.asList(children).iterator();
	}

	private static void dumpCache(ICache c) throws Exception {
		for (String key : c) {
			logger.debugf("dumped key '%s': object is '%s'", key, c.getObject(key));
		}
	}

}
