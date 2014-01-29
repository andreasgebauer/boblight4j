package org.boblight4j.server.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.boblight4j.server.ClientsHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigUpdaterTest {

	private static final Logger LOG = LoggerFactory
			.getLogger(ConfigUpdaterTest.class);

	private List<Device> devices;
	private File file;
	private ConfigUpdater testable;

	@Before
	public void setUp() throws Exception {
		this.file = new File(ConfigUpdaterTest.class.getResource(
				"/boblight.10pc.conf").toURI());
		ClientsHandler clients = Mockito.mock(ClientsHandler.class);

		ConfigCreator configCreator = Mockito.mock(ConfigCreator.class);
		AbstractConfig config = Mockito.mock(AbstractConfig.class);
		this.devices = new ArrayList<Device>();
		final Device device = Mockito.mock(Device.class);
		Mockito.when(device.getName()).thenReturn("arduino");
		Mockito.when(device.getNrChannels()).thenReturn(60);
		this.devices.add(device);
		this.testable = new ConfigUpdater(this.file, configCreator, clients,
				config);
	}

	@Test
	public void testStart() throws IllegalArgumentException,
			IllegalAccessException, InterruptedException {

		final Field stop = WhiteboxImpl.getField(ConfigUpdater.class, "stop");
		stop.setAccessible(true);

		run(stop);

		for (int i = 0; i < 10; i++)
		{
			// again
			stop.set(this.testable, false);

			run(stop);
		}
	}

	private void run(final Field stop) throws InterruptedException {
		this.testable.startThread();

		Thread.sleep(10);

		new Thread(new Runnable() {

			@Override
			public void run() {
				try
				{
					stop.set(ConfigUpdaterTest.this.testable, true);

					final FileWriter fileWriter = new FileWriter(
							ConfigUpdaterTest.this.file, true);
					fileWriter.append('\n');
					fileWriter.close();
				}
				catch (final IOException e)
				{
					LOG.error("", e);
				}
				catch (final IllegalArgumentException e)
				{
					LOG.error("", e);
				}
				catch (final IllegalAccessException e)
				{
					LOG.error("", e);
				}
			}
		}, "filemodifier").start();

		synchronized (this.testable)
		{
			this.testable.wait();
		}

		Assert.assertEquals(1, this.devices.size());

		Assert.assertEquals(60, this.devices.get(0).getNrChannels());
	}
}
