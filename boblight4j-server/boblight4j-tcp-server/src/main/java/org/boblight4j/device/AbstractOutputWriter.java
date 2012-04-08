package org.boblight4j.device;

import java.io.IOException;
import java.io.OutputStream;

public abstract class AbstractOutputWriter implements OutputWriter {

	private int byteCount;
	private final OutputStream output;
	private final Protocol protocol;

	public AbstractOutputWriter(final OutputStream os, final Protocol proto) {
		this.output = os;
		this.protocol = proto;
	}

	public final void flush() throws IOException {
		this.getOutput().flush();
	}

	public final int getByteCount() {
		return this.byteCount;
	}

	protected final int getEndFlag() {
		return this.protocol.getEndFlag();
	}

	protected final int getEscapeFlag() {
		return this.protocol.getEscapeFlag();
	}

	public final OutputStream getOutput() {
		return this.output;
	}

	protected final int getStartFlag() {
		return this.protocol.getStartFlag();
	}

	public final void resetState() {
		this.byteCount = 0;
	}

	@Override
	public void write(final int value) throws IOException {
		this.getOutput().write(value);
		this.byteCount++;
	}

}
