package org.boblight4j.server.config;

import java.util.List;

import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightParseException;
import org.boblight4j.server.ClientsHandler;

public abstract class AbstractConfig implements Config {

	public abstract List<Device> buildDeviceConfig(ClientsHandler clients)
			throws BoblightConfigurationException, BoblightParseException;

	
}
