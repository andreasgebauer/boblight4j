package org.boblight4j.server.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.boblight4j.device.AbstractDevice;
import org.boblight4j.device.Light;
import org.boblight4j.exception.BoblightException;
import org.boblight4j.server.ClientsHandler;

public interface Config {

	/**
	 * Build the color configuration.
	 * 
	 * @return
	 * @throws BoblightException
	 */
	List<Color> buildColorConfig() throws BoblightException;

	void buildConfig(ClientsHandler clients, List<AbstractDevice> devices,
			List<Light> lights) throws BoblightException;

	List<AbstractDevice> buildDeviceConfig(ClientsHandler clients)
			throws BoblightException;

	List<Light> buildLightConfig(List<AbstractDevice> devices, List<Color> colors)
			throws BoblightException;

	boolean checkConfig() throws BoblightException;

	void clearConfig();

	void loadConfigFromFile(File file) throws FileNotFoundException,
			IOException, BoblightException;

}