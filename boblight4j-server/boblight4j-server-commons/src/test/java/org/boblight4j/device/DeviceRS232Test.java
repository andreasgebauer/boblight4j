package org.boblight4j.device;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;

import org.boblight4j.server.Channel;
import org.boblight4j.server.ClientsHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor({ "gnu.io.CommPortIdentifier" })
@PrepareForTest({ DeviceRS232.class, gnu.io.CommPortIdentifier.class })
public class DeviceRS232Test {

	public static final int startFlag = 0x2D;
	private ClientsHandler<?> clients;
	private CommPortIdentifier id;
	private ByteArrayOutputStream outputStream;

	private DeviceRS232 testable;

	private void assertArrayEquals(final byte[] byteArray,
			final byte[] byteArray2) {
		Assert.assertEquals("Unexpected length of both arrays",
				byteArray.length, byteArray2.length);

		for (int i = 0; i < byteArray.length; i++) {
			Assert.assertEquals(byteArray[i], byteArray2[i]);
		}
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		this.clients = Mockito.mock(ClientsHandler.class);

		this.testable = new DeviceRS232(this.clients);
		this.testable.setOutput("/dev/ttyACM0");

		this.setUpCommPort();

		this.setUpOutput(this.id);
	}

	private void setUpCommPort() {
		PowerMockito.mockStatic(gnu.io.CommPortIdentifier.class);

		this.id = Mockito.mock(CommPortIdentifier.class);
		Mockito.when(this.id.getName()).thenReturn("/dev/ttyACM0");

	}

	private void setUpOutput(final CommPortIdentifier id)
			throws PortInUseException, IOException {
		final CommPort commPort = Mockito.mock(SerialPort.class);
		Mockito.when(id.open(Matchers.anyString(), Matchers.anyInt()))
				.thenReturn(commPort);
		this.outputStream = new ByteArrayOutputStream();
		Mockito.when(commPort.getOutputStream()).thenReturn(this.outputStream);
	}

	@Test
	public void testCloseDevice() {
		this.testable.close();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSetupDevice() {
		Enumeration<CommPortIdentifier> enumeration = Mockito
				.mock(Enumeration.class);
		Mockito.when(CommPortIdentifier.getPortIdentifiers()).thenReturn(
				enumeration);
		Mockito.when(enumeration.hasMoreElements()).thenReturn(true, false);
		Mockito.when(enumeration.nextElement()).thenReturn(this.id);

		Assert.assertFalse("Device setup should be unsuccessful but isn't.",
				this.testable.setup());

		this.testable.getProtocol().setStartFlag(0xff);
		this.testable.getProtocol().setEscapeFlag(0x99);
		this.testable.getProtocol().setEndFlag(0x33);

		enumeration = Mockito.mock(Enumeration.class);
		Mockito.when(CommPortIdentifier.getPortIdentifiers()).thenReturn(
				enumeration);
		Mockito.when(enumeration.hasMoreElements()).thenReturn(true, false);
		Mockito.when(enumeration.nextElement()).thenReturn(this.id);

		Assert.assertTrue("Device setup should be successful but isn't.",
				this.testable.setup());
	}

	@Test
	public void testSync() {
		this.testable.sync();
	}

	@Test
	public void testWriteOutput() throws Exception {

		this.testSetupDevice();

		this.outputStream.reset();

		this.testable.writeOutput();

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(0xff);
		baos.write(0x33);

		this.assertArrayEquals(baos.toByteArray(),
				this.outputStream.toByteArray());

		// cleanup
		baos.reset();
		this.outputStream.reset();

		final Channel channel = new Channel(0, 0, "channel");
		channel.setValue(1.0f);
		this.testable.addChannel(channel);

		this.testable.writeOutput();

		baos.write(0xff);
		baos.write(0x00);
		baos.write(0x33);

		this.assertArrayEquals(baos.toByteArray(),
				this.outputStream.toByteArray());

		// cleanup
		baos.reset();
		this.outputStream.reset();

		channel.setValue(1.0f);

		this.testable.writeOutput();

		baos.write(0xff);
		baos.write(0x99);
		baos.write(0xff);
		baos.write(0x33);

		this.assertArrayEquals(baos.toByteArray(),
				this.outputStream.toByteArray());
	}
}
