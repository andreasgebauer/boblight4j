package org.boblight4j.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.boblight4j.exception.BoblightCommunicationException;
import org.boblight4j.server.config.Channel;
import org.boblight4j.server.config.Device;
import org.boblight4j.server.config.Light;
import org.boblight4j.utils.MBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractClientsHandler<T extends ConnectedClient>
		implements Server<T>, ClientsHandler<T> {

	private static final Logger LOG = LoggerFactory
			.getLogger(AbstractClientsHandler.class);

	private static final int FD_SETSIZE = 1024;

	static final String PROTOCOLVERSION = "5";

	private Object mutex = new Object();

	private List<T> clients = new ArrayList<T>();
	private final List<Light> lights;

	public AbstractClientsHandler(final List<Light> lights) {
		if (lights == null) {
			throw new IllegalArgumentException(
					"Argument lights cannot be null.");
		}

		this.lights = lights;

		// register jmx bean
		MBeanUtils.registerBean("org.boblight.server.config:type=Lights",
				new LightsAccessor(this.lights));

	}

	protected abstract void writeFull(T client) throws IOException;

	@Override
	public final void addClient(final T client) throws IOException {

		// clean disconnected clients before adding new
		for (Iterator<T> it = this.getClients().iterator(); it.hasNext();) {
			T cl = it.next();
			if (!cl.isConnected() && !cl.isConnectionPending()) {
				it.remove();
			}
		}

		if (this.getClients().size() >= FD_SETSIZE) // maximum number of clients
		// reached
		{
			LOG.error(String.format("number of clients reached maximum %d",
					FD_SETSIZE));
			writeFull(client);
			this.getClients().remove(client);
			return;
		}

		synchronized (this.mutex) {
			client.setLights(this.lights);
			this.getClients().add(client);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.boblight4j.server.ClientsHandler#removeClient(java.nio.channels.
	 * SocketChannel)
	 */
	@Override
	public void removeClient(final T socketChannel) {
		synchronized (this.mutex) {
			final Iterator<T> iterator = this.getClients().iterator();
			while (iterator.hasNext()) {
				final T client = iterator.next();

				if (client.equals(socketChannel)) {
					client.disconnect();
					iterator.remove();
					return;
				}
			}
		}
	}

	protected void sendPing(final T client)
			throws BoblightCommunicationException {

		int lightsused = 0;

		// CLock lock(m_mutex);
		synchronized (this.mutex) {

			lightsused = client.isOneLightUsed() ? 1 : 0;
		}
		// lock.Leave();
		doSendPing(client, lightsused);
	}

	protected void sendVersion(final T client)
			throws BoblightCommunicationException {
		doSendVersion(client, PROTOCOLVERSION);
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
		for (int i = 0; i < this.getClients().size(); i++) {
			final ConnectedClient client = this.getClients().get(i);
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

			final ConnectedClient cClient = this.getClients().get(clientnr);
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

			for (int j = 0; j < this.getClients().size(); j++) {
				final ConnectedClient cClient = this.getClients().get(j);
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

	public List<T> getClients() {
		return clients;
	}

}
