package org.boblight4j.server;

import org.boblight4j.exception.BoblightCommunicationException;

public interface Server<T extends ConnectedClient> {

	void doSendVersion(final T client, String version)
			throws BoblightCommunicationException;

	void doSendPing(final T client, int lightsused)
			throws BoblightCommunicationException;

}
