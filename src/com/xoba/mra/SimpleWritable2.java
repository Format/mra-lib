package com.xoba.mra;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public final class SimpleWritable2 implements IWriteable {

	private final RandomAccessFile raf;
	private final FileChannel chan;

	public SimpleWritable2(final RandomAccessFile raf) {
		this.raf = raf;
		this.chan = raf.getChannel();
	}

	public final void write(final byte[] buffer, final int offset,
			final int length) throws IOException {
		raf.write(buffer, offset, length);
	}

	public final void close() throws IOException {
		raf.close();
	}

	public final void force() throws IOException {
		chan.force(true);
	}

	public final void write(final int b) throws IOException {
		raf.write(b);
	}
}
