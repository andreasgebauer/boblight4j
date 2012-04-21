package org.boblight4j.client.video;

import java.util.ArrayList;
import static junit.framework.Assert.*;
import java.util.ServiceLoader;

import org.boblight4j.client.Client;
import org.boblight4j.client.grabber.Grabber;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

	@Rule
	public ExpectedException thrown = ExpectedException.none();

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

	@Test
	public void testGetImageGrabberThrowsException() {
		thrown.expect(RuntimeException.class);

		testable.getImageGrabber(null, false, 0, 0);
	}

}
