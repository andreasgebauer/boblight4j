package org.boblight4j.device;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.boblight4j.exception.BoblightDeviceException;
import org.boblight4j.server.ClientsHandler;
import org.boblight4j.server.config.Channel;

/**
 * Base class for all devices.
 * 
 * @author agebauer
 * 
 */
public abstract class AbstractDevice implements Runnable {

	public enum DeviceTypes {
		NOTHING, MOMO, ATMO, POPEN, LTBL, SOUND, DIODER, KARATE,
	}

	private static final Logger LOG = Logger.getLogger(AbstractDevice.class);

	private boolean allowSync;
	protected List<Channel> channels = new ArrayList<Channel>();
	protected ClientsHandler clientsHandler;
	private boolean debug;
	private long delayafteropen;

	private long interval;
	private String name;
	private int nrChannels;

	private String output;
	private int rate;
	private boolean stop;
	private DeviceTypes type;

	private float singleChange;

	protected AbstractDevice(final ClientsHandler clients) {
		this.clientsHandler = clients;
		this.type = DeviceTypes.NOTHING;
		this.allowSync = true;
		this.debug = false;
		this.delayafteropen = 0;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public long getDelayAfterOpen() {
		return this.delayafteropen;
	}

	public void setDelayAfterOpen(final long delay) {
		this.delayafteropen = delay;
	}

	public long getInterval() {
		return this.interval;
	}

	public void setInterval(final long usecs) {
		this.interval = usecs;
	}

	public int getNrChannels() {
		return this.nrChannels;
	}

	public void setNrChannels(final int nrchannels) {
		this.nrChannels = nrchannels;
	}

	public String getOutput() {
		return this.output;
	}

	public void setOutput(final String output) {
		LOG.debug("Setting output to '" + output + "'");
		this.output = output;
	}

	public boolean isAllowSync() {
		return this.allowSync;
	}

	public void setAllowSync(final boolean allowsync) {
		this.allowSync = allowsync;
	}

	public boolean isDebug() {
		return this.debug;
	}

	public void setDebug(final boolean debug) {
		this.debug = debug;
	}

	protected int getRate() {
		return this.rate;
	}

	public void setRate(final int rate) {
		this.rate = rate;
	}

	// virtual
	public void setType(final DeviceTypes momo) {
		this.type = momo;
	}

	public void addChannel(final Channel channel) {
		this.channels.add(channel);
	}

	@Override
	public void run() {
		LOG.info(String.format("%s: starting with output \"%s\"", new Object[] {
				this.name, this.output }));

		while (!this.stop) {
			// keep trying to set up the device every 10 seconds
			while (!this.stop) {
				LOG.info(String.format("%s: setting up", this.name));
				if (!this.setup()) {
					this.close();
					final int millis = 1000;
					LOG.warn(String.format(
							"%s: setting up failed, retrying in " + millis
									+ " ms", this.name));
					try {
						Thread.sleep(millis);
					} catch (final InterruptedException e) {
						LOG.warn("sleep failed", e);
					}
					// USleep(10000000L, m_stop);
				} else {
					LOG.info(String.format("%s: setup succeeded", this.name));
					break;
				}
			}

			// keep calling #writeOutput until we're asked to stop or
			// #writeOutput fails
			while (!this.stop) {
				try {
					this.writeOutput();
				} catch (final BoblightDeviceException e1) {
					LOG.error(this.name, e1);
					break;
				}

				try {
					Thread.sleep(this.interval / 1000);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		LOG.info(String.format("%s: stopped", this.name));
	}

	public void startThread() {
		LOG.info("Starting device '" + this.name + "'");
		new Thread(this, "DeviceThread").start();
	}

	public void stopThread() {
		LOG.info("Stopping device '" + this.name + "'");
		this.stop = true;
	}

	public void asyncStopThread() {
		LOG.info("Stopping device '" + this.name + "'");
		this.stop = true;
	}

	protected abstract boolean setup();

	public abstract void sync();

	protected abstract void close();

	protected abstract void writeOutput() throws BoblightDeviceException;

	public void setSingleChange(float f) {
		this.singleChange = f;
	}

	public float getSingleChange() {
		return this.singleChange;
	}

}
