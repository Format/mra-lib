package com.xoba.mra;

import java.io.IOException;

public interface IWriteable extends ICanClose {

	public void write(byte[] buffer, int offset, int length) throws IOException;

	public void write(int b) throws IOException;

	public void force() throws IOException;

}