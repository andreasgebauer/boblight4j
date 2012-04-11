package org.boblight4j.server;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import org.boblight4j.device.AbstractDevice;
import org.boblight4j.device.Light;
import org.boblight4j.exception.BoblightException;
import org.boblight4j.server.config.Channel;
import org.boblight4j.utils.MessageQueue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

public class ClientsHandlerImplTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	private ClientsHandlerImpl testable;

	@Before
	public void setUp() throws Exception {
		testable = new ClientsHandlerImpl(new ArrayList<Light>());
	}

	private List<Client> getClients() {
		return (List<Client>) Whitebox.getInternalState(testable, "clients");
	}

	@Test
	public void testAddClient() throws IOException {
		final List<Client> clients = getClients();
		final Client client = mock(Client.class);
		final SocketChannel socketChannel = mock(SocketChannel.class);

		// socketChannel.isConnected() will return null, so the first client
		// will be removed when the second is added
		when(client.getSocketChannel()).thenReturn(socketChannel);

		testable.addClient(client);

		assertEquals(1, clients.size());

		testable.addClient(client);

		assertEquals(1, clients.size());
	}

	@Test
	public void testBlockConnect() throws IOException {

		final NioServer nioServer = mock(NioServer.class);
		Whitebox.setInternalState(testable, "nioServer", nioServer);

		testable.blockConnect(true);

		assertEquals(false, Whitebox.getInternalState(nioServer, "accept"));
	}

	@Test
	public void testCleanup() {
		// nothing happens here so there is nothing to test
		testable.cleanup();
	}

	@Test
	public void testFillChannels() throws IOException {
		// setup
		final Client client = new Client(null);
		final ArrayList<Light> lights = new ArrayList<Light>();
		lights.add(new Light());
		Whitebox.setInternalState(testable, "lights", lights);

		// add a client
		testable.addClient(client);

		// fill channels (used/unused)
		testable.fillChannels(new ArrayList<Channel>(), 0,
				mock(AbstractDevice.class));
	}

	@Test
	public void testHandleMessages() throws BoblightException, IOException {
		final Client client = mock(Client.class);
		client.messagequeue = new MessageQueue();

		// add a client
		testable.addClient(client);

		final SocketChannel sockChannel = mock(SocketChannel.class);
		final Socket socket = mock(Socket.class);
		final InetAddress inetAdress = mock(InetAddress.class);
		when(sockChannel.socket()).thenReturn(socket);
		when(socket.getInetAddress()).thenReturn(inetAdress);
		when(client.getSocketChannel()).thenReturn(sockChannel);

		testable.handleMessages(sockChannel, "".getBytes(), 0);
	}

	@Test
	public void testProcess() {
		testable.process();
	}

	@Test
	public void testRemoveClient() {
		testable.removeClient(mock(SocketChannel.class));
	}

	@Test
	public void testSetInterface() {
	}

}
