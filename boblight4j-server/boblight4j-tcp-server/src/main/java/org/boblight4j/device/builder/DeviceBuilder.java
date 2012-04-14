package org.boblight4j.device.builder;

import java.util.List;

import org.boblight4j.device.Device;
import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.server.ClientsHandler;
import org.boblight4j.server.config.ConfigGroup;

public interface DeviceBuilder {

	Device build(int i, ClientsHandler clients, String type)
			throws BoblightConfigurationException;

	Device createDevice(ClientsHandler clientsHandler, int devicenr,
			String type, List<ConfigGroup> deviceLines)
			throws BoblightConfigurationException;
}
