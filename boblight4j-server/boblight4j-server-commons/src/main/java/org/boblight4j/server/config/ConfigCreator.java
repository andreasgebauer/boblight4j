package org.boblight4j.server.config;

import java.util.List;

import org.boblight4j.exception.BoblightException;
import org.boblight4j.server.ClientsHandler;

public interface ConfigCreator {

    Config loadConfig(ClientsHandler<?> clients, Config config) throws BoblightException;

    List<LightConfig> buildLightConfig(List<Device> devices, List<ColorConfig> colors) throws BoblightException;

}
