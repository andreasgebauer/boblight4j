package org.boblight4j.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.util.List;

import org.boblight4j.device.Device;
import org.boblight4j.exception.BoblightException;
import org.boblight4j.server.config.Channel;

public interface ClientsHandler {

	void addClient(Client client) throws IOException;

	/**
	 * Set accept of new clients.
	 * 
	 * @param b
	 *            to accept or to not accept
	 */
	void blockConnect(boolean b);

	void fillChannels(List<Channel> channels, long timestamp, Device device);

	void handleMessages(SocketChannel socketChannel, byte[] array, int numRead)
			throws BoblightException;

	/**
	 * Removes a client by disconnecting it from the server.
	 * 
	 * @param socketChannel
	 *            the socket channel the client uses
	 */
	void removeClient(SocketChannel socketChannel);

	/**
	 * Set the address and port to use.
	 * 
	 * @param address
	 *            the address (host)
	 * @param port
	 *            the port
	 * @throws UnknownHostException
	 *             in case of address cannot be resolved
	 */
	void setInterface(InetAddress address, int port)
			throws UnknownHostException;

}
