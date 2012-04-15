package org.boblight4j.server;

import java.util.List;

import org.boblight4j.server.config.Light;

public interface ConnectedClient {

	boolean isConnected();

	boolean isConnectionPending();

	void disconnect();

	long getConnectTime();

	int getPriority();

	void setLights(List<Light> lights);

	boolean isOneLightUsed();

	List<Light> getLights();

	int lightNameToInt(String lightname);

}
