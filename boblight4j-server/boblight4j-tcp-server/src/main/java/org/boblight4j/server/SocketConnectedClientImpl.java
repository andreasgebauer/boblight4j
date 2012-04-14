package org.boblight4j.server;

import java.nio.channels.SocketChannel;
import java.util.List;

import org.boblight4j.device.Light;
import org.boblight4j.utils.MathUtils;
import org.boblight4j.utils.MessageQueue;

public class SocketConnectedClientImpl implements ConnectedClient {

	long connectTime;
	private List<Light> lights;
	MessageQueue messagequeue = new MessageQueue();
	private int priority;
	private final SocketChannel socketChannel;

	public SocketConnectedClientImpl(final SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public SocketChannel getSocketChannel() {
		return this.socketChannel;
	}

	@Override
	public int lightNameToInt(final String lightname) {
		for (int i = 0; i < this.lights.size(); i++) {
			if (this.lights.get(i).getName().equals(lightname)) {
				return i;
			}
		}
		return -1;
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
}
