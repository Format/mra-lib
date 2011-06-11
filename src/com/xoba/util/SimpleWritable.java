package com.xoba.util;

import java.io.IOException;
import java.io.OutputStream;

public final class SimpleWritable implements IWriteable {

	public SimpleWritable(final OutputStream out) {
		this.out = out;
	}

	private final OutputStream out;

	public final void write(final byte[] buffer, final int offset,
			final int length) throws IOException {
		out.write(buffer, offset, length);
	}

	public final void close() throws IOException {
		out.close();
	}

	public final void force() throws IOException {
		out.flush();
	}

	public final void write(final int b) throws IOException {
		out.write(b);
	}

}
