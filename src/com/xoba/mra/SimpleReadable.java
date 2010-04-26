package com.xoba.mra;

import java.io.IOException;
import java.io.InputStream;

public final class SimpleReadable implements IReadable {

	private final InputStream in;

	public SimpleReadable(final InputStream in) {
		this.in = in;
	}

	public final int read(final byte[] buffer, final int offset, final int length)
			throws IOException {
		return in.read(buffer, offset, length);
	}

	public final void close() throws IOException {
		in.close();
	}

	public final int read(final byte[] buffer) throws IOException {
		return in.read(buffer);
	}

	public int read() throws IOException {
		return in.read();
	}
}
