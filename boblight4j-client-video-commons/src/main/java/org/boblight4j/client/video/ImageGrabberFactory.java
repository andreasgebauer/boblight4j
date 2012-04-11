package org.boblight4j.client.video;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.apache.log4j.Logger;
import org.boblight4j.client.ClientImpl;
import org.boblight4j.client.grabber.Grabber;

/**
 * Factory for Service Provider Implementations (SPI) for classes implementing
 * {@link ImageGrabberServiceProvider}.
 * 
 * @author agebauer
 * 
 */
public class ImageGrabberFactory {

	private static final Logger LOG = Logger
			.getLogger(ImageGrabberFactory.class);

	/**
	 * Instantiates a grabber through Service Provider Implementations.
	 * Therefore all classes implementing {@link ImageGrabberServiceProvider}
	 * are looked up by {@link ServiceLoader}. First implementation wins if
	 * method
	 * {@link ImageGrabberServiceProvider#create(ClientImpl, boolean, int, int)}
	 * returns a non null grabber. If no implementastion can
	 * 
	 * @param client
	 * @param sync
	 * @param width
	 * @param height
	 * @return
	 */
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
				final Grabber grabber = serviceProvider.create(client, sync,
						width, height);
				if (grabber != null)
				{
					return grabber;
				}
			}
		}
		throw new RuntimeException(
				"No ImageGrabberServiceProvider implementation found in classpath.");
	}
}
