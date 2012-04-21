package org.boblight4j.server.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;

import org.boblight4j.exception.BoblightException;
import org.boblight4j.exception.BoblightParseException;
import org.boblight4j.server.ClientsHandler;
import org.boblight4j.server.SocketClientsHandlerImpl;
import org.boblight4j.utils.MBeanUtils;
import org.boblight4j.utils.Misc;
import org.boblight4j.utils.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServerConfigImpl extends AbstractConfig {

	public static final Logger LOG = LoggerFactory
			.getLogger(TcpServerConfigImpl.class);

	/**
	 * Set up where to bind the listening socket. Configuration for this should
	 * already be valid here. We are checking if the configured interface is
	 * reachable.
	 * 
	 * @param clientsHandler
	 *            the clientsHandler interface
	 * @throws BoblightParseException
	 *             in case of
	 * @throws UnknownHostException
	 *             in case of host is not resolvable
	 */
	private void buildClientsHandlerConfig(
			final SocketClientsHandlerImpl clientsHandler)
			throws BoblightParseException, UnknownHostException {
		// empty string means bind to *
		InetAddress ifc = null;
		int port = 19333; // default port
		for (int i = 0; i < this.getGlobalConfigLines().size(); i++) {
			final Pointer<String> line = new Pointer<String>(this
					.getGlobalConfigLines().get(i).line);
			String word = Misc.getWord(line);
			if (word.equals("interface")) {
				ifc = InetAddress.getByName(Misc.getWord(line));
			} else if (word.equals("port")) {
				word = Misc.getWord(line);
				port = Integer.valueOf(word);
			}
		}
		clientsHandler.setInterface(ifc, port);
	}

	public void buildConfig(final ClientsHandler<?> clients,
			final List<Device> devices, final List<Light> lights)
			throws BoblightException {
		LOG.info("building config");

		try {
			this.buildClientsHandlerConfig((SocketClientsHandlerImpl) clients);
		} catch (final UnknownHostException e) {
			// wrap exception
			throw new BoblightException(e);
		}

		final List<Color> colors = this.buildColorConfig();

		final List<Device> tmpDevices = this.buildDeviceConfig(clients);
		final Iterator<Device> iterator = tmpDevices.iterator();
		while (iterator.hasNext()) {
			devices.add(iterator.next());
		}

		final List<Light> tmpLights = this.buildLightConfig(tmpDevices, colors);
		for (final Light light : tmpLights) {
			lights.add(light);

			MBeanUtils.registerBean("org.boblight.server.config:type=Light ["
					+ light.getName() + "]", new LightAccessor(light));

		}

		LOG.info("built config successfully");
	}

}