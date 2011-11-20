package org.boblight4j.client.video;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.apache.log4j.Logger;

public class ImageGrabberFactory {

	private static final Logger LOG = Logger
			.getLogger(ImageGrabberFactory.class);

	public final ImageGrabber getImageGrabber() {
		ServiceLoader<ImageGrabberServiceProvider> load = ServiceLoader
				.load(ImageGrabberServiceProvider.class);

		Iterator<ImageGrabberServiceProvider> iterator = load.iterator();

		while (iterator.hasNext())
		{
			ImageGrabberServiceProvider serviceProvider = iterator.next();

			LOG.debug("Found ImageGrabberServiceProvider implementation "
					+ serviceProvider.getClass().getSimpleName());
			if (serviceProvider != null)
			{
				return serviceProvider.create();
			}
		}
		throw new RuntimeException(
				"No ImageGrabberServiceProvider implementation found in classpath.");
	}
}
