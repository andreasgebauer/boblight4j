package org.boblight4j.device.builder;

import java.util.List;

import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.server.ClientsHandler;
import org.boblight4j.server.config.ConfigGroup;
import org.boblight4j.server.config.Device;

public class LtblBuilder extends AbstractDeviceBuilder {

	public LtblBuilder(final List<ConfigGroup> deviceLines,
			final String filename) {
		super(deviceLines, filename);
	}

	@Override
	public Device createDevice(ClientsHandler<?> clientsHandler, int devicenr,
			String type, List<ConfigGroup> deviceLines)
			throws BoblightConfigurationException {
		throw new BoblightConfigurationException("Not implemented yet.");
	}

}
