package org.boblight4j.server;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.boblight4j.utils.MathUtils;
import org.boblight4j.utils.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketConnectedClientImpl implements ConnectedClient {

	private static final Logger LOG = LoggerFactory
			.getLogger(SocketConnectedClientImpl.class);
	long connectTime;
	MessageQueue messagequeue = new MessageQueue();
	private int priority;
	private final SocketChannel socketChannel;
	private List<Light> lights;
	private Map<String, Light> lightNameMap = new HashMap<String, Light>();

	public SocketConnectedClientImpl(final SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public SocketChannel getSocketChannel() {
		return this.socketChannel;
	}

	public void setPriority(final int priority) {
		this.priority = MathUtils.clamp(priority, 0, 255);
	}

	@Override
	public boolean isConnected() {
		return this.socketChannel.isConnected()
				&& this.socketChannel.socket().getInetAddress() != null;
	}

	@Override
	public boolean isConnectionPending() {
		return this.socketChannel.isConnectionPending();
	}

	@Override
	public void setLights(List<Light> lights) {
		this.lights = lights;
		for (Light light : lights) {
			this.lightNameMap.put(light.getConfig().getName(), light);
		}
	}

	@Override
	public List<Light> getLights() {
		return this.lights;
	}

	@Override
	public long getConnectTime() {
		return this.connectTime;
	}

	@Override
	public int getPriority() {
		return this.priority;
	}

	@Override
	public boolean isOneLightUsed() {
		// check if any light is used
		for (Light light : this.lights) {
			if (light.getNrUsers() > 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void disconnect() {
		final Socket socket = getSocketChannel().socket();
		final SocketAddress remoteAddress = socket.getRemoteSocketAddress();
		LOG.info(String.format("removing %s", remoteAddress));
		try {
			getSocketChannel().close();
			socket.close();
		} catch (final IOException e) {
			LOG.error("Error during Socket.close()", e);
		}
	}

	@Override
	public Light getLight(String lightname) {
		return this.lightNameMap.get(lightname);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (connectTime ^ (connectTime >>> 32));
		result = prime * result
				+ ((socketChannel == null) ? 0 : socketChannel.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SocketConnectedClientImpl other = (SocketConnectedClientImpl) obj;
		if (connectTime != other.connectTime)
			return false;
		if (socketChannel == null) {
			if (other.socketChannel != null)
				return false;
		} else if (!socketChannel.equals(other.socketChannel))
			return false;
		return true;
	}

}
