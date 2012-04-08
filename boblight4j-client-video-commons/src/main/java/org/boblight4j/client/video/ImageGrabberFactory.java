package org.boblight4j.client.video;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.apache.log4j.Logger;
import org.boblight4j.client.ClientImpl;
import org.boblight4j.client.grabber.Grabber;

public class ImageGrabberFactory {

	private static final Logger LOG = Logger
			.getLogger(ImageGrabberFactory.class);

	public final Grabber getImageGrabber(ClientImpl client, boolean sync,
			int width, int height) {
		ServiceLoader<ImageGrabberServiceProvider> load = ServiceLoader
				.load(ImageGrabberServiceProvider.class);

		Iterator<ImageGrabberServiceProvider> iterator = load.iterator();

		while (iterator.hasNext())
		{
			ImageGrabberServiceProvider serviceProvider = iterator.next();
			if (serviceProvider != null)
			{
				LOG.debug("Found ImageGrabberServiceProvider implementation "
						+ serviceProvider.getClass().getSimpleName());
				return serviceProvider.create(client, sync, width, height);
			}
		}
		throw new RuntimeException(
				"No ImageGrabberServiceProvider implementation found in classpath.");
	}
}
