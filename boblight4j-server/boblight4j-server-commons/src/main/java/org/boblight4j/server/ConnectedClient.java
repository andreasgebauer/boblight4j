package org.boblight4j.server;

import java.util.List;

public interface ConnectedClient {

	boolean isConnected();

	boolean isConnectionPending();

	void disconnect();

	long getConnectTime();

	int getPriority();

	void setLights(List<Light> lights);

	boolean isOneLightUsed();

	List<Light> getLights();

	Light getLight(String lightname);

}
