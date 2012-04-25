package org.boblight4j.client.video;

import static junit.framework.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.ServiceLoader;

import org.boblight4j.client.Client;
import org.boblight4j.client.grabber.Grabber;
import org.boblight4j.exception.BoblightRuntimeException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ServiceLoader.class, ImageGrabberFactory.class })
public class ImageGrabberFactoryTest {

	private ImageGrabberFactory testable;

	private ServiceLoader<ImageGrabberServiceProvider> serviceLoader;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		testable = new ImageGrabberFactory();

		PowerMockito.mockStatic(ServiceLoader.class);

		serviceLoader = PowerMockito.mock(ServiceLoader.class);
	}

	@Test
	public void testGetImageGrabber() {
		ArrayList<ImageGrabberServiceProvider> serviceProviders = new ArrayList<ImageGrabberServiceProvider>();
		ImageGrabberServiceProvider serviceProvider = Mockito
				.mock(ImageGrabberServiceProvider.class);
		serviceProviders.add(serviceProvider);

		PowerMockito
				.when(ServiceLoader.load(ImageGrabberServiceProvider.class))
				.thenReturn(serviceLoader);
		PowerMockito.when(serviceLoader.iterator()).thenReturn(
				serviceProviders.iterator());

		Client client = null;
		boolean sync = false;
		int width = 320;
		int height = 240;

		Grabber grabber = Mockito.mock(Grabber.class);
		PowerMockito.when(serviceProvider.create(client, sync, width, height))
				.thenReturn(grabber);

		Grabber imageGrabber = testable.getImageGrabber(client, sync, width,
				height);

		assertNotNull(imageGrabber);
	}

	@Test(expected = BoblightRuntimeException.class)
	public void testGetImageGrabberReturnNoGrabber() {
		ArrayList<ImageGrabberServiceProvider> serviceProviders = new ArrayList<ImageGrabberServiceProvider>();

		PowerMockito
				.when(ServiceLoader.load(ImageGrabberServiceProvider.class))
				.thenReturn(serviceLoader);
		PowerMockito.when(serviceLoader.iterator()).thenReturn(
				serviceProviders.iterator());

		testable.getImageGrabber(null, false, 0, 0);
	}

	@Test(expected = BoblightRuntimeException.class)
	public void testGetImageGrabberThrowsException() {
		ArrayList<ImageGrabberServiceProvider> serviceProviders = new ArrayList<ImageGrabberServiceProvider>();
		ImageGrabberServiceProvider serviceProvider = Mockito
				.mock(ImageGrabberServiceProvider.class);
		serviceProviders.add(serviceProvider);

		PowerMockito
				.when(ServiceLoader.load(ImageGrabberServiceProvider.class))
				.thenReturn(serviceLoader);
		PowerMockito.when(serviceLoader.iterator()).thenReturn(
				serviceProviders.iterator());

		testable.getImageGrabber(null, false, 0, 0);
	}

}
