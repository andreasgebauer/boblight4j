package org.boblight4j.server;

import java.io.IOException;
import java.util.List;

import org.boblight4j.server.config.Channel;
import org.boblight4j.server.config.Device;

public interface ClientsHandler<T extends ConnectedClient> {

	/**
	 * Adds a client
	 * 
	 * @param client
	 * @throws IOException
	 */
	void addClient(T client) throws IOException;

	/**
	 * Set accept of new clients.
	 * 
	 * @param b
	 *            to accept or to not accept
	 */
	void blockConnect(boolean b);

	void fillChannels(List<Channel> channels, long timestamp, Device device);

	/**
	 * Removes a client by disconnecting it from the server.
	 * 
	 * @param socketChannel
	 *            the socket channel the client uses
	 */
	void removeClient(T socketChannel);

}
