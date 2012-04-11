package org.boblight4j.server.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.boblight4j.device.AbstractDevice;
import org.boblight4j.device.Light;
import org.boblight4j.exception.BoblightException;
import org.boblight4j.server.ClientsHandler;

public abstract class AbstractConfigUpdater {

	private static final Logger LOG = Logger
			.getLogger(AbstractConfigUpdater.class);

	private final ClientsHandler clients;
	private final Config config;
	private final List<AbstractDevice> devices;
	private final List<Light> lights;
	protected File watchFile;

	public AbstractConfigUpdater(final File watchFile,
			final ClientsHandler clients, final Config config,
			final List<AbstractDevice> devices, final List<Light> lights) {
		super();
		this.config = config;
		this.devices = devices;
		this.lights = lights;
		this.clients = clients;
		this.watchFile = watchFile;
	}

	protected final synchronized void updateConfig() {

		this.clients.blockConnect(true);

		// Iterator<Device> iterator = devices.iterator();
		// while (iterator.hasNext()) {
		// Device next = iterator.next();
		// next.stopThread();
		// }

		// devices.clear();
		// lights.clear();

		this.config.clearConfig();

		// load and parse config
		try
		{
			this.config.loadConfigFromFile(this.watchFile);

			this.config.checkConfig();

			final List<AbstractDevice> newDevices = this.config
					.buildDeviceConfig(this.clients);
			final Vector<AbstractDevice> unhandledNewDevices = new Vector<AbstractDevice>();

			for (final AbstractDevice newDevice : newDevices)
			{
				boolean oldDeviceFound = false;
				for (final AbstractDevice oldDevice : this.devices)
				{
					if (newDevice.getName().equals(oldDevice.getName()))
					{
						oldDeviceFound = true;
						oldDevice.setAllowSync(newDevice.isAllowSync());
						oldDevice.setDebug(newDevice.isDebug());
						oldDevice.setDelayAfterOpen(newDevice
								.getDelayAfterOpen());
						oldDevice.setInterval(newDevice.getInterval());
					}
				}

				if (!oldDeviceFound)
				{
					unhandledNewDevices.add(newDevice);
				}
			}

			final List<Color> newColors = this.config.buildColorConfig();
			// for (Color color : newColors) {
			// boolean oldDeviceFound = false;
			// for (Color oldDevice : this.devices) {
			// if (newDevice.GetName().equals(oldDevice.GetName())) {
			// oldDeviceFound = true;
			// oldDevice.SetAllowSync(newDevice.isAllowSync());
			// oldDevice.SetDebug(newDevice.isDebug());
			// oldDevice.SetDelayAfterOpen(newDevice
			// .getDelayAfterOpen());
			// oldDevice.SetInterval(newDevice.getInterval());
			// }
			// }
			//
			// if (!oldDeviceFound) {
			// unhandledNewDevices.add(newDevice);
			// }
			// }

			final List<Light> unhandledNewLights = new Vector<Light>();
			final List<Light> newLights = this.config.buildLightConfig(
					this.devices, newColors);
			for (final Light newLight : newLights)
			{
				boolean oldLightFound = false;
				for (final Light oldLight : this.lights)
				{
					if (newLight.getName().equals(oldLight.getName()))
					{
						oldLightFound = true;
						oldLight.setHscan(newLight.getHscan());
						oldLight.setVscan(newLight.getVscan());

						for (int i = 0; i < oldLight.getNrColors(); i++)
						{
							oldLight.setColor(i, newColors.get(i));
						}
					}
				}

				if (!oldLightFound)
				{
					unhandledNewLights.add(newLight);
				}
			}

			// config.buildConfig(clients, devices, lights);

		}
		catch (final FileNotFoundException e)
		{
			LOG.error("Error during config update.", e);
		}
		catch (final IOException e)
		{
			LOG.error("Error during config update.", e);
		}
		catch (final BoblightException e)
		{
			LOG.error("Error during config update.", e);
		}

		// iterator = devices.iterator();
		// while (iterator.hasNext()) {
		// Device next = iterator.next();
		// next.startThread();
		// }

		this.clients.blockConnect(false);
	}
}
