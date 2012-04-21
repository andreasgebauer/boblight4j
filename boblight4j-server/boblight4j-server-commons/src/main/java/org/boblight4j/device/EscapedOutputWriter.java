package org.boblight4j.device;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Output writer implementation for writing escaped output.
 * 
 * @author agebauer
 * 
 */
public class EscapedOutputWriter extends AbstractOutputWriter {

	private static final Logger LOG = LoggerFactory
			.getLogger(EscapedOutputWriter.class);
	private final boolean debug;

	/**
	 * Constructs an output writer.
	 * 
	 * @param os
	 *            the output stream
	 * @param proto
	 *            the protocol to use
	 */
	public EscapedOutputWriter(final OutputStream os, final Protocol proto,
			final boolean debug) {
		super(os, proto);
		this.debug = debug;
	}

	@Override
	public void begin() throws IOException {

		if (this.debug) {
			final StringBuilder buf = new StringBuilder();
			final String format = String.format("%x", this.getStartFlag())
					.toUpperCase(Locale.ENGLISH);
			if (format.length() == 1) {
				buf.append("0");
			}
			buf.append(format);
			buf.append(" ");
			LOG.debug(buf.toString());
		}

		this.getOutput().write(this.getStartFlag());
	}

	@Override
	public void end() throws IOException {

		if (this.debug) {
			final StringBuilder buf = new StringBuilder();
			final String format = String.format("%x", this.getEndFlag())
					.toUpperCase(Locale.ENGLISH);
			if (format.length() == 1) {
				buf.append("0");
			}
			buf.append(format);
			buf.append(" ");
			LOG.debug(buf.toString());
		}

		this.getOutput().write(this.getEndFlag());
		// getOutput().flush();
	}

	@Override
	public void write(final int value) throws IOException {
		StringBuilder buf = null;
		if (this.debug) {
			buf = new StringBuilder();
		}

		if (value == this.getEndFlag() || value == this.getEscapeFlag()
				|| value == this.getStartFlag()) {
			if (this.debug) {
				final String format = String.format("%x", this.getEscapeFlag())
						.toUpperCase(Locale.ENGLISH);
				if (format.length() == 1) {
					buf.append("0");
				}
				buf.append(format);
			}
			this.getOutput().write(this.getEscapeFlag());
		}

		if (this.debug) {
			final String format = String.format("%x", value).toUpperCase(
					Locale.ENGLISH);
			if (format.length() == 1) {
				buf.append("0");
			}
			buf.append(format);
			buf.append(" ");
			LOG.debug(buf.toString());
		}

		this.getOutput().write(value);
	}
}
