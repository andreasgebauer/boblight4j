package org.boblight4j.server;

import java.nio.channels.SocketChannel;
import java.util.List;

import org.boblight4j.device.Light;
import org.boblight4j.utils.MathUtils;
import org.boblight4j.utils.MessageQueue;

public class Client {

	long connectTime;
	List<Light> lights;
	MessageQueue messagequeue = new MessageQueue();
	int priority;
	private final SocketChannel socketChannel;

	public Client(final SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public SocketChannel getSocketChannel() {
		return this.socketChannel;
	}

	public int lightNameToInt(final String lightname) {
		for (int i = 0; i < this.lights.size(); i++)
		{
			if (this.lights.get(i).getName().equals(lightname))
			{
				return i;
			}
		}
		return -1;
	}

	public void setPriority(final int priority) {
		this.priority = MathUtils.clamp(priority, 0, 255);
	}
}
