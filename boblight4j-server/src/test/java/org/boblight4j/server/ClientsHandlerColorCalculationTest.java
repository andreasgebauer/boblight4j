package org.boblight4j.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.boblight4j.device.Device;
import org.boblight4j.device.Light;
import org.boblight4j.exception.BoblightException;
import org.boblight4j.server.config.Channel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ClientsHandlerColorCalculationTest {

	private Light bottom1;
	private Channel bottom1blue;
	private Channel bottom1green;
	private Channel bottom1red;
	private List<Channel> channels;
	private Client client;
	private Device device;
	private ClientsHandlerImpl testable;

	private void nextStep(final long time, final double expected) {
		double value;
		this.testable.fillChannels(this.channels, time, this.device);

		value = this.bottom1red.getValue(time);
		Assert.assertEquals(expected, value, 0.0001);
	}

	private void setLight(final float d, final float e, final float f,
			final int timeDiff) {
		this.bottom1.setRgb(new float[] { d, e, f }, timeDiff);
	}

	@Before
	public void setUp() throws Exception {
		final List<Light> vector = new ArrayList<Light>();

		this.bottom1 = new Light();
		this.bottom1.setInterpolation(true);
		this.bottom1.setName("bottom1");
		this.bottom1.addColor(ColorUtils.red());
		this.bottom1.addColor(ColorUtils.green());
		this.bottom1.addColor(ColorUtils.blue());
		vector.add(this.bottom1);

		this.device = Mockito.mock(Device.class);

		this.testable = new ClientsHandlerImpl(vector);

		this.channels = new ArrayList<Channel>();
		this.bottom1red = new Channel(0, 0);
		this.bottom1red.setValue(1.0f);
		this.channels.add(this.bottom1red);

		this.bottom1green = new Channel(1, 0);
		this.bottom1green.setValue(1.0f);
		this.channels.add(this.bottom1green);

		this.bottom1blue = new Channel(2, 0);
		this.bottom1blue.setValue(1.0f);
		this.channels.add(this.bottom1blue);

		final SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
		final Socket socket = Mockito.mock(Socket.class);
		Mockito.when(socketChannel.socket()).thenReturn(socket);
		final InetAddress inetAddress = Mockito.mock(InetAddress.class);
		Mockito.when(socket.getInetAddress()).thenReturn(inetAddress);

		this.client = new Client(socketChannel);
		this.testable.addClient(this.client);

	}

	@Test
	public void testCleanup() {
		this.testable.cleanup();
	}

	@Test
	public void testFillChannelsSpeed100() throws IOException {

		this.bottom1red.setSpeed(100);
		this.bottom1.setSpeed(100);

		// set the color of bottom1 to white
		this.bottom1.setRgb(new float[] { 1, 1, 1 }, 1);

		this.nextStep(1, 0);

		// set the color of bottom1 to white
		this.bottom1.setRgb(new float[] { 1, 1, 1 }, 2);

		this.nextStep(2, 1);

		// set the color of bottom1 to white
		this.bottom1.setRgb(new float[] { 1, 1, 1 }, 3);

		this.nextStep(3, 1);

		// set the color of bottom1 to white
		this.bottom1.setRgb(new float[] { 1, 1, 1 }, 4);

		this.nextStep(4, 1);

		// set the color of bottom1 to black
		this.bottom1.setRgb(new float[] { 0, 0, 0 }, 5);

		this.nextStep(5, 1);

		// set the color of bottom1 to black
		this.bottom1.setRgb(new float[] { 0, 0, 0 }, 6);

		this.nextStep(6, 0);

	}

	@Test
	public void testFillChannelsSpeed50() throws IOException, BoblightException {

		this.bottom1red.setSpeed(50);
		this.bottom1.setSpeed(50);

		// set the color of bottom1 to white
		this.setLight(1.0f, 1.0f, 1.0f, 0);
		this.nextStep(this.bottom1.getTime() + 1, 0);

		// set the color of bottom1 to white
		this.setLight(1.0f, 1.0f, 1.0f, 1);
		this.nextStep(this.bottom1.getTime() + 1, 0.0034);

		// set the color of bottom1 to white
		this.setLight(1.0f, 1.0f, 1.0f, 2);

		this.nextStep(this.bottom1.getTime() + 1, 0.007);

		// set the color of bottom1 to white
		this.setLight(1.0f, 1.0f, 1.0f, 3);

		this.nextStep(this.bottom1.getTime() + 1, 0.0104);

		// set the color of bottom1 to white
		this.setLight(1.0f, 1.0f, 1.0f, 4);

		this.nextStep(this.bottom1.getTime() + 1, 0.0138);

		// set the color of bottom1 to black
		this.setLight(0.0f, 0.0f, 0.0f, 5);

		this.nextStep(this.bottom1.getTime() + 1, 0.0138);

		// set the color of bottom1 to black
		this.setLight(0.0f, 0.0f, 0.0f, 6);

		this.nextStep(this.bottom1.getTime() + 1, 0.0138);

		// set the color of bottom1 to black
		this.setLight(0.0f, 0.0f, 0.0f, 7);

		this.nextStep(this.bottom1.getTime() + 1, 0.0137);

		// set the color of bottom1 to black
		this.setLight(0.0f, 0.0f, 0.0f, 8);

		this.nextStep(this.bottom1.getTime() + 1, 0.0136);

		this.nextStep(70, 0.011);

		this.nextStep(700, 0.0012);

	}

	@Test
	public void testHandleMessages() throws BoblightException, IOException {

		final Light e = new Light();
		e.setName("bottom1");

		final byte[] bytes = "set light bottom1 rgb 1.0 1.0 1.0\n".getBytes();

		this.testable.handleMessages(this.client.getSocketChannel(), bytes,
				bytes.length);

	}

	@Test
	public void testProcess() {
		this.testable.process();
	}
}
