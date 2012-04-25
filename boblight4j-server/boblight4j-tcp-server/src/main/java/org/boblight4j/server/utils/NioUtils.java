package org.boblight4j.server.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioUtils {

	private NioUtils() {
	}

	public static void write(final SocketChannel chnl, final String message)
			throws IOException {
		chnl.write(ByteBuffer.wrap(message.getBytes()));
		chnl.socket().getOutputStream().flush();
	}

}
