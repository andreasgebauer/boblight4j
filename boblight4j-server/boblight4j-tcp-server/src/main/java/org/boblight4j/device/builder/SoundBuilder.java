package org.boblight4j.device.builder;

import java.util.List;

import org.boblight4j.device.Device;
import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.server.ClientsHandler;
import org.boblight4j.server.config.ConfigGroup;

public class SoundBuilder extends AbstractDeviceBuilder {

	public SoundBuilder(final List<ConfigGroup> deviceLines,
			final String filename) {
		super(deviceLines, filename);
	}

	@Override
	public final Device build(final int i, final ClientsHandler clients,
			final String type) throws BoblightConfigurationException {
		throw new BoblightConfigurationException("Not implemented yet.");
	}

}
