package org.boblight4j.device.builder;

import org.boblight4j.device.AbstractDevice;
import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.server.ClientsHandler;

public interface DeviceBuilder {

	AbstractDevice build(int i, ClientsHandler clients, String type)
			throws BoblightConfigurationException;
}
