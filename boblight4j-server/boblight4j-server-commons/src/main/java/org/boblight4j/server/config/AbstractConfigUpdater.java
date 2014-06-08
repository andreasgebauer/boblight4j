package org.boblight4j.server.config;

import java.io.File;

import org.boblight4j.exception.BoblightException;
import org.boblight4j.server.ClientsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConfigUpdater {

    private static final Logger LOG = LoggerFactory
	    .getLogger(AbstractConfigUpdater.class);

    private final ClientsHandler<?> clients;
    private final AbstractConfig config;
    protected File watchFile;

    private final ConfigCreator configCreator;

    public AbstractConfigUpdater(final File watchFile,
	    ConfigCreator configCreator, final ClientsHandler<?> clients,
	    final AbstractConfig config) {
	super();
	this.configCreator = configCreator;
	this.config = config;
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

	this.config.clear();

	// load and parse config
	try {

	    Config loadConfig = this.configCreator.loadConfig(clients, config);

	    // this.config.checkConfig();

	    // final List<Device> newDevices = this.config
	    // .buildDeviceConfig(this.clients);
	    // final List<Device> unhandledNewDevices = new ArrayList<Device>();
	    //
	    // for (final Device newDevice : newDevices) {
	    // boolean oldDeviceFound = false;
	    // for (final Device oldDevice : this.devices) {
	    // if (newDevice.getName().equals(oldDevice.getName())) {
	    // oldDeviceFound = true;
	    // oldDevice.setAllowSync(newDevice.isAllowSync());
	    // oldDevice.setDebug(newDevice.isDebug());
	    // oldDevice.setDelayAfterOpen(newDevice
	    // .getDelayAfterOpen());
	    // oldDevice.setInterval(newDevice.getInterval());
	    // }
	    // }
	    //
	    // if (!oldDeviceFound) {
	    // unhandledNewDevices.add(newDevice);
	    // }
	    // }

	    // final List<Color> newColors = this.config.buildColorConfig();
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

	    // final List<Light> unhandledNewLights = new Vector<Light>();
	    // final List<Light> newLights = this.config.buildLightConfig(
	    // this.devices, newColors);
	    // for (final Light newLight : newLights) {
	    // boolean oldLightFound = false;
	    // for (final Light oldLight : this.lights) {
	    // if (newLight.getName().equals(oldLight.getName())) {
	    // oldLightFound = true;
	    // oldLight.setHscan(newLight.getHscan());
	    // oldLight.setVscan(newLight.getVscan());
	    //
	    // for (int i = 0; i < oldLight.getNrColors(); i++) {
	    // oldLight.setColor(i, newColors.get(i));
	    // }
	    // }
	    // }
	    //
	    // if (!oldLightFound) {
	    // unhandledNewLights.add(newLight);
	    // }
	    // }

	    // config.buildConfig(clients, devices, lights);

	    // } catch (final FileNotFoundException e) {
	    // LOG.error("Error during config update.", e);
	    // } catch (final IOException e) {
	    // LOG.error("Error during config update.", e);
	} catch (final BoblightException e) {
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
