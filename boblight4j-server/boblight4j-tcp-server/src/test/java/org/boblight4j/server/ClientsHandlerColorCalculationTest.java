package org.boblight4j.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import org.boblight4j.exception.BoblightException;
import org.boblight4j.server.config.Channel;
import org.boblight4j.server.config.Device;
import org.boblight4j.server.config.Light;
import org.boblight4j.utils.MessageQueue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ClientsHandlerColorCalculationTest {

	private Light bottom1;
	private SocketConnectedClientImpl client;
	private SocketClientsHandlerImpl testable;

	@Before
	public void setUp() throws Exception {
		final List<Light> vector = new ArrayList<Light>();

		this.bottom1 = new Light();
		this.bottom1.setInterpolation(true);
		this.bottom1.setName("bottom1");
		this.bottom1.addColor(ColorUtils.red());
		this.bottom1.addColor(ColorUtils.green());
		this.bottom1.addColor(ColorUtils.blue());
		vector.add(this.bottom1);

		this.testable = new SocketClientsHandlerImpl(vector);

		final SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
		final Socket socket = Mockito.mock(Socket.class);
		Mockito.when(socketChannel.socket()).thenReturn(socket);
		final InetAddress inetAddress = Mockito.mock(InetAddress.class);
		Mockito.when(socket.getInetAddress()).thenReturn(inetAddress);

		this.client = Mockito.mock(SocketConnectedClientImpl.class);
		Mockito.when(client.isConnected()).thenReturn(true);
		Mockito.when(client.getSocketChannel()).thenReturn(socketChannel);
		Mockito.when(client.getLights()).thenReturn(vector);
		this.testable.addClient(this.client);

		this.client.messagequeue = new MessageQueue();
	}

	@Test
	public void testCleanup() {
		this.testable.cleanup();
	}

	@Test
	public void testHandleMessages() throws BoblightException, IOException {

		final Light e = new Light();
		e.setName("bottom1");

		final byte[] bytes = "set light bottom1 rgb 1.0 1.0 1.0\n".getBytes();

		this.testable.handleMessages(this.client.getSocketChannel(), bytes,
				bytes.length);

	}

	@Test
	public void testProcess() {
		this.testable.process();
	}
}
