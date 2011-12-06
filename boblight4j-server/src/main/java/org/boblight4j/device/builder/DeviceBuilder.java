package org.boblight4j.device.builder;

import org.boblight4j.device.Device;
import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.server.ClientsHandler;

public interface DeviceBuilder {

	Device build(int i, ClientsHandler clients, String type)
			throws BoblightConfigurationException;
}
