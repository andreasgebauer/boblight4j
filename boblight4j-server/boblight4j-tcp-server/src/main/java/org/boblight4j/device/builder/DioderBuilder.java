package org.boblight4j.device.builder;

import java.util.List;

import org.boblight4j.device.AbstractDevice;
import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.server.ClientsHandler;
import org.boblight4j.server.config.ConfigGroup;

public class DioderBuilder extends AbstractDeviceBuilder {

	public DioderBuilder(final List<ConfigGroup> devicelines,
			final String filename) {
		super(devicelines, filename);
	}

	@Override
	public final AbstractDevice build(final int i, final ClientsHandler clients,
			final String type) throws BoblightConfigurationException {
		throw new BoblightConfigurationException("Not implemented yet.");
	}

}
