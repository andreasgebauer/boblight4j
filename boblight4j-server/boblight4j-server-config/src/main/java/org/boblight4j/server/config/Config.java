package org.boblight4j.server.config;

import java.util.List;

public interface Config {

	void clear();

	void setDevices(List<Device> devices);

	void setLights(List<LightConfig> lights);

}