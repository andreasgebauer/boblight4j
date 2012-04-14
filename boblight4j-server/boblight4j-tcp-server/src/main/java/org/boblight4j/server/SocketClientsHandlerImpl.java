package org.boblight4j.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.boblight4j.device.AbstractDevice;
import org.boblight4j.device.Device;
import org.boblight4j.device.Light;
import org.boblight4j.exception.BoblightCommunicationException;
import org.boblight4j.exception.BoblightException;
import org.boblight4j.exception.BoblightParseException;
import org.boblight4j.server.config.Channel;
import org.boblight4j.server.utils.NioUtils;
import org.boblight4j.utils.MBeanUtils;
import org.boblight4j.utils.Message;
import org.boblight4j.utils.Misc;
import org.boblight4j.utils.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketClientsHandlerImpl extends
		AbstractClientsHandler<SocketConnectedClientImpl> implements ClientsHandler {

	private static final int FD_SETSIZE = 1024;

	private static final Logger LOG = LoggerFactory
			.getLogger(SocketClientsHandlerImpl.class);
	private static final int MAXDATA = 100000;

	private InetAddress address;

	private final List<SocketConnectedClientImpl> clients = new ArrayList<SocketConnectedClientImpl>();
	private final List<Light> lights;

	private final Object mutex = new Object();

	private NioServer nioServer;

	private int port;

	public SocketClientsHandlerImpl(final List<Light> lights)
			throws IOException {
		if (lights == null) {
			throw new IllegalArgumentException(
					"Argument lights cannot be null.");
		}

		this.lights = lights;

		// register jmx bean
		MBeanUtils.registerBean("org.boblight.server.config:type=Lights",
				new LightsAccessor(this.lights));

	}

	@Override
	public final void addClient(final ConnectedClient client)
			throws IOException {

		// clean disconnected clients before adding new
		for (Iterator<SocketConnectedClientImpl> it = this.clients.iterator(); it
				.hasNext();) {
			ConnectedClient cl = it.next();
			if (!cl.isConnected() && !cl.isConnectionPending()) {
				it.remove();
			}
		}

		if (this.clients.size() >= FD_SETSIZE) // maximum number of clients
												// reached
		{
			LOG.error(String.format("number of clients reached maximum %d",
					FD_SETSIZE));
			NioUtils.write((SocketConnectedClientImpl) client, "full\n");
			this.clients.remove(client);
			return;
		}

		synchronized (this.mutex) {
			client.setLights(this.lights);
			this.clients.add((SocketConnectedClientImpl) client);
		}
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

	@Override
	public final void fillChannels(final List<Channel> channels,
			final long time, final Device device) {

		final Collection<Light> usedLights = doFillChannels(channels, time,
				device);

		// reset singlechange
		Iterator<Light> usedLgthsIt = usedLights.iterator();
		while (usedLgthsIt.hasNext()) {
			final Light light = usedLgthsIt.next();
			light.resetSingleChange(device);
		}

		// update which lights we're using
		for (int i = 0; i < this.clients.size(); i++) {
			final ConnectedClient client = this.clients.get(i);
			for (int j = 0; j < client.getLights().size(); j++) {
				boolean lightUsed = false;

				final Light clientLight = client.getLights().get(j);

				usedLgthsIt = usedLights.iterator();
				while (usedLgthsIt.hasNext()) {
					final Light usedLight = usedLgthsIt.next();
					if (usedLight.equals(clientLight)) {
						lightUsed = true;
						break;
					}
				}

				if (lightUsed) {
					clientLight.addUser(device);
				} else {
					clientLight.clearUser(device);
				}
			}
		}
	}

	private Collection<Light> doFillChannels(final List<Channel> channels,
			final long time, final Device device) {
		List<Light> usedLights = new ArrayList<Light>();
		for (int i = 0; i < channels.size(); i++) {
			// get the oldest client with the highest priority
			final Channel channel = channels.get(i);
			final int light = channel.getLight();
			final int color = channel.getColor();

			if (light == -1 || color == -1) {
				continue;
			}

			int clientnr = chooseClient(light);

			if (clientnr == -1) // no client for the light on this channel
			{
				channel.setUsed(false);
				channel.setSpeed(this.lights.get(light).getSpeed());
				channel.setValueToFallback();
				channel.setGamma(1.0);
				channel.setAdjust(1.0);
				channel.setBlacklevel(0.0);
				continue;
			}

			// fill channel with values from the client
			channel.setUsed(true);

			final ConnectedClient cClient = this.clients.get(clientnr);
			final Light cLight = cClient.getLights().get(light);

			final float colorValue = cLight.getColorValue(color, time);

			channel.setValue(colorValue);
			channel.setSpeed(cLight.getSpeed());
			channel.setGamma(cLight.getGamma(color));
			channel.setAdjust(cLight.getAdjust(color));
			channel.setAdjusts(cLight.getAdjusts(color));
			channel.setBlacklevel(cLight.getBlacklevel(color));
			channel.setSingleChange(cLight.getSingleChange(device));

			// save light because we have to reset the singlechange later
			// more than one channel can use a light so can't do this from the
			// loop
			usedLights.add(cLight);
		}
		return usedLights;
	}

	private int chooseClient(final int light) {
		int clientnr = -1;
		{
			long clienttime = Long.MAX_VALUE;
			int priority = 255;

			for (int j = 0; j < this.clients.size(); j++) {
				final ConnectedClient cClient = this.clients.get(j);
				final Light cLight = cClient.getLights().get(light);

				if (cClient.getPriority() == 255
						|| cClient.getConnectTime() == -1 || !cLight.isUse()) {
					// this client we don't use
					continue;
				}

				// this client has a high priority (lower number) than the
				// current one, or has the same and is older
				if (cClient.getPriority() < priority
						|| priority == cClient.getPriority()
						&& cClient.getConnectTime() < clienttime) {
					clientnr = j;
					clienttime = cClient.getConnectTime();
					priority = cClient.getPriority();
				}
			}
		}
		return clientnr;
	}

	@Override
	public void handleMessages(final SocketChannel socketChannel,
			final byte[] bs, final int numRead) throws BoblightException {
		SocketConnectedClientImpl client = null;
		final Iterator<SocketConnectedClientImpl> iterator = this.clients.iterator();
		while (iterator.hasNext()) {
			final SocketConnectedClientImpl selected = iterator.next();
			final InetAddress inetAddress2 = socketChannel.socket()
					.getInetAddress();
			if (!selected.isConnected() && !selected.isConnectionPending()) {
				LOG.info("Removing client " + selected);
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
			final Message message = client.messagequeue.getMessage();
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
				NioUtils.write(client, "hello\n");
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
		String lightname;
		String lightkey;
		int lightnr = -1;

		lightname = Misc.getWord(message.message);
		lightkey = Misc.getWord(message.message);
		int lightIdx = lightnr = client.lightNameToInt(lightname);
		if (lightIdx == -1) {
			throw new BoblightParseException("Unable to resolve lightname '"
					+ lightname + "' to index.");
		}

		String value = null;
		try {
			final Light cLight = client.getLights().get(lightnr);
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
				cLight.setRgb(rgb, message.time);
			} else if (lightkey.equals("speed")) {
				value = Misc.getWord(message.message);
				final float speed = Float.parseFloat(value);
				cLight.setSpeed(speed);
			} else if (lightkey.equals("interpolation")) {
				value = Misc.getWord(message.message);
				final boolean interpolation = Boolean.parseBoolean(value);
				cLight.setInterpolation(interpolation);
			} else if (lightkey.equals("use")) {
				value = Misc.getWord(message.message);
				final boolean use = Boolean.parseBoolean(value);
				cLight.setUse(use);
			} else if (lightkey.equals("singlechange")) {
				value = Misc.getWord(message.message);
				final float singlechange = Float.parseFloat(value);
				cLight.setSingleChange(singlechange);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.boblight4j.server.ClientsHandler#removeClient(java.nio.channels.
	 * SocketChannel)
	 */
	@Override
	public void removeClient(final ConnectedClient socketChannel) {
		SocketConnectedClientImpl remoteClient = (SocketConnectedClientImpl) socketChannel;
		synchronized (this.mutex) {
			final Iterator<SocketConnectedClientImpl> iterator = this.clients
					.iterator();
			while (iterator.hasNext()) {
				final SocketConnectedClientImpl client = iterator.next();
				if (client.getSocketChannel().equals(socketChannel)) {
					final Socket socket = remoteClient.getSocketChannel()
							.socket();
					final SocketAddress remoteAddress = socket
							.getRemoteSocketAddress();
					LOG.info(String.format("removing %s", remoteAddress));
					try {
						remoteClient.getSocketChannel().close();
						socket.close();
					} catch (final IOException e) {
						LOG.error("Error during Socket.close()", e);
					}
					iterator.remove();
					return;
				}
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
			msg.append("light " + client.getLights().get(i).getName() + " ");

			msg.append("scan ");
			msg.append(client.getLights().get(i).getVscan()[0] + " ");
			msg.append(client.getLights().get(i).getVscan()[1] + " ");
			msg.append(client.getLights().get(i).getHscan()[0] + " ");
			msg.append(client.getLights().get(i).getHscan()[1]);
			msg.append("\n");
		}

		try {
			NioUtils.write(client, msg.toString());
		} catch (final IOException e) {
			LOG.error("", e);
			return false;
		}

		return true;
	}

	@Override
	public void doSendPing(final SocketConnectedClientImpl client, int lightsused)
			throws BoblightCommunicationException {
		try {
			NioUtils.write(client, "ping " + lightsused + "\n");
		} catch (final IOException e) {
			throw new BoblightCommunicationException(e);
		}
	}

	@Override
	public void doSendVersion(final SocketConnectedClientImpl client, String version)
			throws BoblightCommunicationException {
		try {
			NioUtils.write(client, "version " + version + "\n");
		} catch (final IOException e) {
			throw new BoblightCommunicationException(e);
		}
	}

	public void setInterface(final InetAddress address, final int port)
			throws UnknownHostException {
		this.address = address;
		if (this.address == null) {
			this.address = InetAddress.getByName("0.0.0.0");
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
		for (SocketConnectedClientImpl client : this.clients) {
			if (client.getSocketChannel().socket().getInetAddress()
					.equals(socketChannel.socket().getInetAddress())) {
				return client;
			}
		}
		return null;
	}
}
