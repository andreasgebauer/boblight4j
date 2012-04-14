package org.boblight4j.device;

import java.io.IOException;

public interface OutputWriter {

	/**
	 * 
	 * @throws IOException
	 */
	void begin() throws IOException;

	void end() throws IOException;

	void write(int value) throws IOException;

}
