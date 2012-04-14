package org.boblight4j.server;

import java.util.List;

import org.boblight4j.device.Light;

public interface ConnectedClient {

	boolean isConnected();

	boolean isConnectionPending();

	long getConnectTime();

	int getPriority();

	void setLights(List<Light> lights);

	List<Light> getLights();

	int lightNameToInt(String lightname);

}
