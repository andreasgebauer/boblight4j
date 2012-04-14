package org.boblight4j.client.jmf;

import java.util.Arrays;
import java.util.List;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoblightJMF {

	private static final Logger LOG = LoggerFactory.getLogger(BoblightJMF.class);

	public static void main(final String[] args) {
		try
		{
			final BoblightJMF boblightJMF = new BoblightJMF();
			boblightJMF.init();
		}
		catch (final Exception e)
		{
			LOG.error("", e);
		}
	}

	private void init() {
		final CaptureDeviceInfo devInfo = new CaptureDeviceInfo();

		CaptureDeviceManager.addDevice(devInfo);
		final List<?> deviceList = CaptureDeviceManager
				.getDeviceList(new Format("YUYV"));

		LOG.info(Arrays.toString(deviceList.toArray()));
	}
}
