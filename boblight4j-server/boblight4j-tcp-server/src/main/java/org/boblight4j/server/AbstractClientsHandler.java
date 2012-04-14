package org.boblight4j.server;

import org.boblight4j.exception.BoblightCommunicationException;

public abstract class AbstractClientsHandler<T extends ConnectedClient> implements Server<T> {

	static final String PROTOCOLVERSION = "5";

	private Object mutex = new Object();

	protected void sendPing(final T client)
			throws BoblightCommunicationException {

		int lightsused = 0;

		// CLock lock(m_mutex);
		synchronized (this.mutex) {
			// check if any light is used
			for (int i = 0; i < client.getLights().size(); i++) {
				if (client.getLights().get(i).getNrUsers() > 0) {
					lightsused = 1;
					break; // if one light is used we have enough info
				}
			}
		}
		// lock.Leave();
		doSendPing(client, lightsused);
	}

	protected void sendVersion(final T client)
			throws BoblightCommunicationException {
		doSendVersion(client, PROTOCOLVERSION);
	}

}
