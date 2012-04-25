package org.boblight4j.server.config;

import static org.mockito.Mockito.mock;

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

	private ClientsHandler<?> clients;
	private List<Device> devices;
	private File file;
	private ConfigUpdater testable;

	private ConfigCreator configCReator;

	@Before
	public void setUp() throws Exception {
		this.file = new File(ConfigUpdaterTest.class.getResource(
				"/boblight.10pc.conf").toURI());
		this.clients = Mockito.mock(ClientsHandler.class);

		AbstractConfig config = Mockito.mock(AbstractConfig.class);
		this.devices = new ArrayList<Device>();
		final Device device = Mockito.mock(Device.class);
		Mockito.when(device.getName()).thenReturn("arduino");
		Mockito.when(device.getNrChannels()).thenReturn(60);
		this.devices.add(device);
		this.configCReator = mock(ConfigCreator.class);
		this.testable = new ConfigUpdater(this.file, this.configCReator,
				this.clients, config);
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
				try {
					stop.set(ConfigUpdaterTest.this.testable, true);

					final FileWriter fileWriter = new FileWriter(
							ConfigUpdaterTest.this.file, true);
					fileWriter.append('\n');
					fileWriter.close();
				} catch (final IOException e) {
					LOG.error("", e);
				} catch (final IllegalArgumentException e) {
					LOG.error("", e);
				} catch (final IllegalAccessException e) {
					LOG.error("", e);
				}
			}
		}, "filemodifier").start();

		synchronized (this.testable) {
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
				try {
					stop.set(ConfigUpdaterTest.this.testable, true);

					final FileWriter fileWriter = new FileWriter(
							ConfigUpdaterTest.this.file, true);
					fileWriter.append('\n');
					fileWriter.close();
				} catch (final IOException e) {
					LOG.error("", e);
				} catch (final IllegalArgumentException e) {
					LOG.error("", e);
				} catch (final IllegalAccessException e) {
					LOG.error("", e);
				}
			}
		}, "filemodifier").start();

		synchronized (this.testable) {
			this.testable.wait();
		}

		Assert.assertEquals(1, this.devices.size());

		Assert.assertEquals(60, this.devices.get(0).getNrChannels());

	}
}
