package org.boblight4j.client.jmf;

import java.util.Arrays;
import java.util.List;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;

import org.apache.log4j.Logger;
import org.boblight4j.client.AbstractFlagManager;
import org.boblight4j.client.Client;
import org.boblight4j.client.grabber.AbstractPassiveGrabber;
import org.boblight4j.exception.BoblightException;

public class JMFVideoGrabber extends AbstractPassiveGrabber {

	public JMFVideoGrabber(Client client, boolean sync, int width, int height) {
		super(client, sync, width, height);
	}

	private static final Logger LOG = Logger.getLogger(JMFVideoGrabber.class);

	@Override
	public void setup(final AbstractFlagManager flagManager)
			throws BoblightException {
		@SuppressWarnings("unchecked")
		final List<CaptureDeviceInfo> deviceList = CaptureDeviceManager
				.getDeviceList(null);
		LOG.info(Arrays.toString(deviceList.toArray()));
	}

	@Override
	public void cleanup() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
