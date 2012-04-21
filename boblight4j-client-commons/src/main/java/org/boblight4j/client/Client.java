package org.boblight4j.client;

import java.io.IOException;

import org.boblight4j.exception.BoblightException;
import org.boblight4j.utils.Pointer;

public interface Client {

	void connect(String address, int port, int i) throws BoblightException;

	int getNrLights();

	void addPixel(int i, int y, int[] rgb);

	void addPixel(int i, int[] js) throws BoblightException;

	String getLightName(int j) throws BoblightException;

	/**
	 * Sets a boblight option.
	 * 
	 * @param lightnr
	 * @param option
	 * @throws BoblightException
	 */

	void setOption(int lightnr, String option) throws BoblightException;

	void setScanRange(int width, int height);

	void sendRgb(boolean sync, final Pointer<Integer> outputused)
			throws IOException, BoblightException;

	void setPriority(int priority) throws BoblightException;

	void destroy();

}
