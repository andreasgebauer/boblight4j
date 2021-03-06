package org.boblight4j.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.boblight4j.exception.BoblightCommunicationException;
import org.boblight4j.exception.BoblightException;
import org.boblight4j.server.config.Device;
import org.boblight4j.server.config.LightConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ClientsHandlerColorCalculationTest {

	private LightConfig bottom;
	private Light bottom1;
	private Channel bottom1blue;
	private Channel bottom1green;
	private Channel bottom1red;
	private List<Channel> channels;
	private ConnectedClient client;
	private Device device;
	private AbstractClientsHandler<ConnectedClient> testable;

	@Before
	public void setUp() throws Exception {
		ArrayList<LightConfig> value = new ArrayList<LightConfig>();
		final List<Light> lights = new ArrayList<Light>();

		bottom = new LightConfig("bottom1");
		this.bottom1 = new Light(bottom, false);
		this.bottom1.setInterpolation(true);
		bottom.addColor(ColorUtils.red());
		bottom.addColor(ColorUtils.green());
		bottom.addColor(ColorUtils.blue());
		value.add(bottom);
		lights.add(bottom1);

		this.device = Mockito.mock(Device.class);

		this.testable = new AbstractClientsHandler<ConnectedClient>() {

			@Override
			public void doSendVersion(ConnectedClient client, String version)
					throws BoblightCommunicationException {
			}

			@Override
			public void doSendPing(ConnectedClient client, int lightsused)
					throws BoblightCommunicationException {
			}

			@Override
			public void blockConnect(boolean b) {
			}

			@Override
			protected void writeFull(ConnectedClient client) throws IOException {
			}
		};
		testable.createLights(value);

		this.channels = new ArrayList<Channel>();
		this.bottom1red = new Channel(0, 0, "bottom1");
		this.bottom1red.setValue(1.0f);
		this.channels.add(this.bottom1red);

		this.bottom1green = new Channel(1, 0, "bottom1");
		this.bottom1green.setValue(1.0f);
		this.channels.add(this.bottom1green);

		this.bottom1blue = new Channel(2, 0, "bottom1");
		this.bottom1blue.setValue(1.0f);
		this.channels.add(this.bottom1blue);

		this.client = Mockito.mock(ConnectedClient.class);
		Mockito.when(client.isConnected()).thenReturn(true);
		Mockito.when(client.getLights()).thenReturn(lights);
		Mockito.when(client.getLight("bottom1")).thenReturn(bottom1);

		this.testable.addClient(this.client);

	}

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

		this.nextStep(this.bottom1.getTime() + 1, 0.0172);

		// set the color of bottom1 to black
		this.setLight(0.0f, 0.0f, 0.0f, 6);

		this.nextStep(this.bottom1.getTime() + 1, 0.0172);

		// set the color of bottom1 to black
		this.setLight(0.0f, 0.0f, 0.0f, 7);

		this.nextStep(this.bottom1.getTime() + 1, 0.0171);

		// set the color of bottom1 to black
		this.setLight(0.0f, 0.0f, 0.0f, 8);

		this.nextStep(this.bottom1.getTime() + 1, 0.0170);

		this.nextStep(70, 0.0138);

		this.nextStep(700, 0.0015);

	}

}
