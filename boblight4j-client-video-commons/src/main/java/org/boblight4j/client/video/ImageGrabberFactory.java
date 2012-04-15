package org.boblight4j.client.video;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.boblight4j.client.Client;
import org.boblight4j.client.grabber.Grabber;
import org.boblight4j.exception.BoblightRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for Service Provider Implementations (SPI) for classes implementing
 * {@link ImageGrabberServiceProvider}.
 * 
 * @author agebauer
 * 
 */
public class ImageGrabberFactory {

	private static final Logger LOG = LoggerFactory
			.getLogger(ImageGrabberFactory.class);

	/**
	 * Instantiates a grabber through Service Provider Implementations.
	 * Therefore all classes implementing {@link ImageGrabberServiceProvider}
	 * are looked up by {@link ServiceLoader}. First implementation wins if
	 * method
	 * {@link ImageGrabberServiceProvider#create(Client, boolean, int, int)}
	 * returns a non null grabber. If no implementation can be found a
	 * {@link BoblightRuntimeException} is thrown.
	 * 
	 * @param client
	 *            the client
	 * @param sync
	 *            sync mode allowed
	 * @param width
	 *            the grabbing width
	 * @param height
	 *            the grabbing height
	 * @return an image grabber
	 * @throws BoblightRuntimeException
	 *             in case of no service provider can be found which returns a
	 *             non-null grabber
	 */
	public final Grabber getImageGrabber(Client client, boolean sync,
			int width, int height) {
		ServiceLoader<ImageGrabberServiceProvider> load = ServiceLoader
				.load(ImageGrabberServiceProvider.class);

		Iterator<ImageGrabberServiceProvider> iterator = load.iterator();

		while (iterator.hasNext()) {
			ImageGrabberServiceProvider serviceProvider = iterator.next();
			if (serviceProvider != null) {
				LOG.debug("Found ImageGrabberServiceProvider implementation "
						+ serviceProvider.getClass().getSimpleName());
				final Grabber grabber = serviceProvider.create(client, sync,
						width, height);
				if (grabber != null) {
					return grabber;
				}
			}
		}
		throw new BoblightRuntimeException(
				"No ImageGrabberServiceProvider implementation found in classpath.");
	}
}
