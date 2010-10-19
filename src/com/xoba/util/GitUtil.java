package com.xoba.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

public class GitUtil {

	private static final ILogger logger = LogFactory.getDefault().create();

	public static void main(String[] args) throws Exception {
		logger.debugf("got version: '%s'", getVersionUUID());
	}

	private static UUID uuid;

	public static synchronized UUID getVersionUUID() {
		if (uuid == null) {
			try {
				InputStream in = MraUtils.getResourceInputStream(GitUtil.class.getPackage(), "git-commit.txt");
				if (in != null) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					try {
						String line = reader.readLine();
						byte[] hex = MraUtils.convertFromHex(line);
						uuid = MraUtils.marshalUUIDFromBytes(hex);
					} finally {
						reader.close();
					}
				}

			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				if (uuid == null) {
					uuid = UUID.randomUUID();
				}
			}
		}
		return uuid;
	}
}
