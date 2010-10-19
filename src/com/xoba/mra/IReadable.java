package com.xoba.mra;

import java.io.IOException;

public interface IReadable extends ICanClose {

	public int read() throws IOException;

	public int read(byte[] buffer) throws IOException;

	public int read(byte[] buffer, int offset, int length) throws IOException;

}