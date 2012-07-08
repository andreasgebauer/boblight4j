package org.boblight4j.server.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConfig implements Config {

	private static final Logger LOG = LoggerFactory
			.getLogger(AbstractConfig.class);

	private List<Device> devices;
	private List<LightConfig> lights;

	private final String fileName;

	public AbstractConfig(String fileName) {
		this.fileName = fileName;
	}

	public List<Device> getDevices() {
		return devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}

	public List<LightConfig> getLights() {
		return lights;
	}

	public void setLights(List<LightConfig> lights) {
		this.lights = lights;
	}

	public void clearConfig() {
		// TODO implement
	}

}
