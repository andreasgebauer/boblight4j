package org.boblight4j.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.boblight4j.exception.BoblightCommunicationException;
import org.boblight4j.exception.BoblightException;
import org.boblight4j.exception.BoblightParseException;
import org.boblight4j.server.config.Device;
import org.boblight4j.server.config.LightConfig;
import org.boblight4j.server.utils.NioUtils;
import org.boblight4j.utils.Message;
import org.boblight4j.utils.Misc;
import org.boblight4j.utils.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketClientsHandlerImpl extends
		AbstractClientsHandler<SocketConnectedClientImpl> {

	private static final Logger LOG = LoggerFactory
			.getLogger(SocketClientsHandlerImpl.class);
	private static final int MAXDATA = 100000;

	private InetAddress address;

	private final Object mutex = new Object();

	private NioServer nioServer;

	private int port;

	public SocketClientsHandlerImpl() throws IOException {
	}

	@Override
	public final void blockConnect(final boolean doBlock) {
		if (this.nioServer == null) {
			throw new IllegalStateException("NioServer not initialized.");
		}
		this.nioServer.blockAccept(doBlock);
	}

	public void cleanup() {
		// TODO Auto-generated method stub
	}

	public void handleMessages(final SocketChannel socketChannel,
			final byte[] bs, final int numRead) throws BoblightException {
		SocketConnectedClientImpl client = null;
		final Iterator<SocketConnectedClientImpl> iterator = this.getClients()
				.iterator();
		while (iterator.hasNext()) {
			final SocketConnectedClientImpl selected = iterator.next();
			final InetAddress inetAddress2 = socketChannel.socket()
					.getInetAddress();
			// remove disconnected clients
			if (!selected.isConnected() && !selected.isConnectionPending()) {
				LOG.info("Removing client " + selected);
				selected.disconnect();
				iterator.remove();
				continue;
			}
			if (selected.getSocketChannel().socket().getInetAddress()
					.equals(inetAddress2)) {
				client = selected;
			}
		}

		client.messagequeue.addData(new String(bs, 0, numRead));

		// client sent too much data
		if (client.messagequeue.getRemainingDataSize() > MAXDATA) {
			throw new BoblightException(String.format("%s sent too much data",
					client.getSocketChannel().socket().getInetAddress()));
		}

		// loop until there are no more messages
		while (client.messagequeue.getNrMessages() > 0) {
			final Message message = client.messagequeue.nextMessage();
			try {
				this.parseMessage(client, message);
			} catch (final BoblightParseException e) {
				final InetAddress inetAddress = client.getSocketChannel()
						.socket().getInetAddress();
				LOG.error(String.format("%s sent gibberish.", inetAddress), e);
			}
		}
	}

	private void parseGet(final SocketConnectedClientImpl client,
			final Message message) throws BoblightCommunicationException,
			BoblightParseException {

		final String messagekey = Misc.getWord(message.message);
		if (messagekey.equals("version")) {
			this.sendVersion(client);
		} else if (messagekey.equals("lights")) {
			this.sendLights(client);
		} else {
			throw new BoblightParseException(String.format(
					"Expected: version|lights  Actual: %s", messagekey));
		}
	}

	private void parseMessage(final SocketConnectedClientImpl client,
			final Message message) throws BoblightParseException,
			BoblightCommunicationException {
		final Pointer<String> messagekeyPtr = new Pointer<String>();
		// an empty message is invalid
		String messagekey = messagekeyPtr.get();

		messagekey = Misc.getWord(message.message);

		// throw new BoblightException(
		// String.format("%s sent gibberish: %s", client
		// .getSocketChannel().socket()
		// .getRemoteSocketAddress(), messagekeyPtr.get()));

		if (messagekey.equals("hello")) {
			final SocketAddress inetAddress = client.getSocketChannel()
					.socket().getRemoteSocketAddress();
			LOG.info(String.format("%s said hello", inetAddress));
			try {
				NioUtils.write(client.getSocketChannel(), "hello\n");
			} catch (final IOException e) {
				throw new BoblightCommunicationException(e);
			}

			if (client.connectTime == -1) {
				client.connectTime = message.time;
			}

		} else if (messagekey.equals("ping")) {
			this.sendPing(client);
		} else if (messagekey.equals("get")) {
			this.parseGet(client, message);
		} else if (messagekey.equals("set")) {
			this.parseSet(client, message);
		} else if (messagekey.equals("sync")) {
			this.parseSync(client);
		} else {
			throw new BoblightParseException(String.format(
					"Expected: hello|ping|get|set|sync Actual: %s", messagekey));
		}
	}

	private void parseSet(final SocketConnectedClientImpl client,
			final Message message) throws BoblightParseException {
		final String messagekey = Misc.getWord(message.message);

		if (messagekey.equals("priority")) {
			final String strpriority = Misc.getWord(message.message);
			try {
				final int priority = Integer.valueOf(strpriority);
				synchronized (this.mutex) {
					client.setPriority(priority);
				}
				LOG.info(String.format("%s priority set to %d", client
						.getSocketChannel().socket().getRemoteSocketAddress(),
						priority));
			} catch (final NumberFormatException e) {
				throw new BoblightParseException(
						"Expected Integer value for set priority value '"
								+ strpriority + "'", e);
			}

		} else if (messagekey.equals("light")) {
			this.parseSetLight(client, message);
		} else {
			throw new BoblightParseException(String.format(
					"Expected: priority|light  Actual: %s", messagekey));
		}
	}

	private void parseSetLight(final ConnectedClient client,
			final Message message) throws BoblightParseException {
		String lightname = Misc.getWord(message.message);
		String lightkey = Misc.getWord(message.message);

		final Light clrCalc = client.getLight(lightname);
		if (clrCalc == null) {
			throw new BoblightParseException("Unable to resolve lightname '"
					+ lightname + "' to index.");
		}
		String value = null;
		try {
			if (lightkey.equals("rgb")) {
				final float rgb[] = new float[3];
				for (int i = 0; i < 3; i++) {
					try {
						value = Misc.getWord(message.message);
					} catch (final BoblightParseException e) {
						throw new BoblightParseException(
								String.format(
										"Expected 3 values for rgb but got just %d",
										i), e);
					}
					rgb[i] = Float.valueOf(value);
				}
				clrCalc.setRgb(rgb, message.time);
			} else if (lightkey.equals("speed")) {
				value = Misc.getWord(message.message);
				final float speed = Float.parseFloat(value);
				clrCalc.setSpeed(speed);
			} else if (lightkey.equals("interpolation")) {
				value = Misc.getWord(message.message);
				final boolean interpolation = Boolean.parseBoolean(value);
				clrCalc.setInterpolation(interpolation);
			} else if (lightkey.equals("use")) {
				value = Misc.getWord(message.message);
				final boolean use = Boolean.parseBoolean(value);
				clrCalc.setUse(use);
			} else if (lightkey.equals("singlechange")) {
				value = Misc.getWord(message.message);
				final float singlechange = Float.parseFloat(value);
				clrCalc.setSingleChange(singlechange);
			} else {
				throw new BoblightParseException(String.format(
						"%s sent gibberish", client));
			}
		} catch (final NumberFormatException e) {
			throw new BoblightParseException(String.format(
					"%s sent gibberish: %s", client, value), e);
		}

	}

	private void parseSync(final ConnectedClient client) {
		final Set<Device> users = new HashSet<Device>();

		synchronized (this.mutex) {
			// build up a list of devices using this client's input
			for (int i = 0; i < client.getLights().size(); i++) {
				final Light cLight = client.getLights().get(i);
				for (int j = 0; j < cLight.getNrUsers(); j++) {
					users.add(cLight.getUser(j));
				}
			}
		}

		// remove duplicates
		// users.sort();
		// users.unique();

		final Iterator<Device> iterator = users.iterator();
		while (iterator.hasNext()) {
			iterator.next().sync();
		}
	}

	public void process() {

		// open listening socket if it's not already
		if (this.nioServer == null) {
			LOG.info(String.format("opening listening socket on %s:%d",
					this.address, this.port));
			try {
				this.nioServer = new NioServer(this.address, this.port, this);
				new Thread(this.nioServer, "NioServer").start();
			} catch (final Exception e) {
				LOG.error("Error during NioServer setup", e);
				this.nioServer.stop();
			}
		}

	}

	/**
	 * Sends light info, like name and area
	 * 
	 * @param client
	 * @return
	 */
	private boolean sendLights(final SocketConnectedClientImpl client) {
		// build up messages by appending to CTcpData
		final StringBuilder msg = new StringBuilder("lights "
				+ client.getLights().size() + "\n");

		for (int i = 0; i < client.getLights().size(); i++) {
			LightConfig config = client.getLights().get(i).getConfig();
			msg.append("light " + config.getName() + " ");

			msg.append("scan ");
			msg.append(config.getVscan()[0] + " ");
			msg.append(config.getVscan()[1] + " ");
			msg.append(config.getHscan()[0] + " ");
			msg.append(config.getHscan()[1]);
			msg.append("\n");
		}

		try {
			NioUtils.write(client.getSocketChannel(), msg.toString());
		} catch (final IOException e) {
			LOG.error("", e);
			return false;
		}

		return true;
	}

	@Override
	public void doSendPing(final SocketConnectedClientImpl client,
			int lightsused) throws BoblightCommunicationException {
		try {
			NioUtils.write(client.getSocketChannel(), "ping " + lightsused + "\n");
		} catch (final IOException e) {
			throw new BoblightCommunicationException(e);
		}
	}

	@Override
	public void doSendVersion(final SocketConnectedClientImpl client,
			String version) throws BoblightCommunicationException {
		try {
			NioUtils.write(client.getSocketChannel(), "version " + version + "\n");
		} catch (final IOException e) {
			throw new BoblightCommunicationException(e);
		}
	}

	public void setInterface(final InetAddress address, final int port)
			throws UnknownHostException {
		if (address == null) {
			LOG.warn("Defaulting to ip address 0.0.0.0 because argument address was null.");
			this.address = InetAddress.getByName("0.0.0.0");
		} else {
			this.address = address;
		}
		this.port = port;
	}

	public void removeClient(SocketChannel socketChannel) {
		SocketConnectedClientImpl client = getClient(socketChannel);
		if (client == null) {
			throw new IllegalArgumentException("No client with socketChannel "
					+ socketChannel + " available.");
		} else {
			removeClient(client);
		}
	}

	private SocketConnectedClientImpl getClient(SocketChannel socketChannel) {
		for (SocketConnectedClientImpl client : this.getClients()) {
			if (client.getSocketChannel().socket().getInetAddress()
					.equals(socketChannel.socket().getInetAddress())) {
				return client;
			}
		}
		return null;
	}

	@Override
	protected void writeFull(SocketConnectedClientImpl client)
			throws IOException {
		NioUtils.write(client.getSocketChannel(), "full\n");
	}

}
