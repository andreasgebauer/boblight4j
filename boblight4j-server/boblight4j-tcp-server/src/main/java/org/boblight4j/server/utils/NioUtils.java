package org.boblight4j.server.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.boblight4j.server.SocketConnectedClientImpl;

public class NioUtils {

	private NioUtils() {
	}

	public static void write(final SocketConnectedClientImpl client, final String message)
			throws IOException {
		final SocketChannel chnl = client.getSocketChannel();
		chnl.write(ByteBuffer.wrap(message.getBytes()));
		chnl.socket().getOutputStream().flush();
	}

}
