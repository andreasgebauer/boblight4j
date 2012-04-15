package org.boblight4j.server.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.boblight4j.exception.BoblightException;

public interface Config {

	/**
	 * Build the color configuration.
	 * 
	 * @return
	 * @throws BoblightException
	 */
	List<Color> buildColorConfig() throws BoblightException;

	List<Light> buildLightConfig(List<Device> devices, List<Color> colors)
			throws BoblightException;

	boolean checkConfig() throws BoblightException;

	void clearConfig();

	void loadConfigFromFile(File file) throws FileNotFoundException,
			IOException, BoblightException;

	List<ConfigLine> getGlobalConfigLines();

}