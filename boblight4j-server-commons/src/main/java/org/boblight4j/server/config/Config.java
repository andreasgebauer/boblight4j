package org.boblight4j.server.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.boblight4j.device.Device;
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

	void buildConfig(ClientsHandler clients, List<Device> devices,
			List<Light> lights) throws BoblightException;

	List<Device> buildDeviceConfig(ClientsHandler clients)
			throws BoblightException;

	List<Light> buildLightConfig(List<Device> devices, List<Color> colors)
			throws BoblightException;

	boolean checkConfig() throws BoblightException;

	void clearConfig();

	void loadConfigFromFile(String file) throws FileNotFoundException,
			IOException, BoblightException;

}