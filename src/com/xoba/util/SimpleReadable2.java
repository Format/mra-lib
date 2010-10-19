package com.xoba.util;

import java.io.IOException;
import java.io.RandomAccessFile;

public final class SimpleReadable2 implements IReadable {

	private final RandomAccessFile raf;

	public SimpleReadable2(final RandomAccessFile raf) {
		this.raf = raf;
	}

	public final int read(final byte[] buffer, final int offset,
			final int length) throws IOException {
		return raf.read(buffer, offset, length);
	}

	public final void close() throws IOException {
		raf.close();
	}

	public final int read(final byte[] buffer) throws IOException {
		return raf.read(buffer);
	}

	public int read() throws IOException {
		return raf.read();
	}
}
