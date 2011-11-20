package org.boblight4j.client.jmf;

import java.util.Arrays;
import java.util.List;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;

import org.apache.log4j.Logger;
import org.boblight4j.client.AbstractFlagManager;
import org.boblight4j.client.Client;
import org.boblight4j.client.video.ImageGrabber;
import org.boblight4j.exception.BoblightException;

public class JMFVideoGrabber implements ImageGrabber {

	private static final Logger LOG = Logger.getLogger(JMFVideoGrabber.class);

	@Override
	public void cleanup() {

	}

	@Override
	public void run() throws BoblightException {

	}

	@Override
	public void setup(final AbstractFlagManager flagManager, final Client client)
			throws BoblightException {
		@SuppressWarnings("unchecked")
		final List<CaptureDeviceInfo> deviceList = CaptureDeviceManager
				.getDeviceList(null);
		LOG.info(Arrays.toString(deviceList.toArray()));
	}

}
