package org.boblight4j.device;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleOutputWriter extends AbstractOutputWriter {

	private static final Logger LOG = LoggerFactory
			.getLogger(SimpleOutputWriter.class);

	private final boolean debug;

	public SimpleOutputWriter(final OutputStream os, final Protocol proto,
			final boolean debug) {
		super(os, proto);
		this.debug = debug;
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
		if (this.debug) {
			LOG.debug(this.getByteCount() + " Bytes written.");
		}
	}
}
