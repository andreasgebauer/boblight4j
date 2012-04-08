package org.boblight4j.device;

import java.io.IOException;
import java.io.OutputStream;

public class SimpleOutputWriter extends AbstractOutputWriter {

	public SimpleOutputWriter(final OutputStream os, final Protocol proto) {
		super(os, proto);
	}

	@Override
	public void begin() throws IOException {
		super.resetState();
		this.write(this.getStartFlag());
	}

	@Override
	public void end() throws IOException {
		this.write(this.getEndFlag());
		this.flush();
		System.out.println(this.getByteCount() + " Bytes written.");
	}

}
