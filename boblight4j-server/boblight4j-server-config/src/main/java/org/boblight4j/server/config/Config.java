package org.boblight4j.server.config;

import java.util.List;

import org.boblight4j.exception.BoblightException;

public interface Config {

	void clearConfig();

	void setDevices(List<Device> devices);

	void setLights(List<LightConfig> lights);

}