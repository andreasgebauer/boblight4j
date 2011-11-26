package org.boblight4j.server.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.boblight4j.device.Device;
import org.boblight4j.device.Light;
import org.boblight4j.server.ClientsHandlerImpl;
import org.boblight4j.server.NioServer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.internal.WhiteboxImpl;

public class ConfigUpdaterTest {

	private static final Logger LOG = Logger.getLogger(ConfigUpdaterTest.class);

	private ClientsHandlerImpl clients;
	private List<Device> devices;
	private File file;
	private List<Light> lights;
	private ConfigUpdater testable;

	@Before
	public void setUp() throws Exception {
		this.file = new File(ConfigUpdaterTest.class.getResource(
				"/boblight.10pc.conf").toURI());
		this.lights = new ArrayList<Light>();
		this.clients = new ClientsHandlerImpl(this.lights);

		final Field field = WhiteboxImpl.getField(ClientsHandlerImpl.class,
				"nioServer");
		field.set(this.clients, Mockito.mock(NioServer.class));

		ConfigImpl config = new ConfigImpl();
		this.devices = new ArrayList<Device>();
		final Device device = Mockito.mock(Device.class);
		Mockito.when(device.getName()).thenReturn("arduino");
		Mockito.when(device.getNrChannels()).thenReturn(60);
		this.devices.add(device);
		this.testable = new ConfigUpdater(this.file, this.clients, config,
				this.devices, this.lights);
	}

	@Test
	public void testStart() throws IllegalArgumentException,
			IllegalAccessException, InterruptedException {

		final Field stop = WhiteboxImpl.getField(ConfigUpdater.class, "stop");
		stop.setAccessible(true);

		this.testable.startThread();

		Thread.sleep(500);

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

		// again
		stop.set(this.testable, false);

		this.testable.startThread();

		Thread.sleep(500);

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
