package org.boblight4j.client;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;

import junit.framework.Assert;

import org.boblight4j.client.mbean.LightConfigMBean;
import org.boblight4j.exception.BoblightException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SocketClientImpl.class, SocketChannel.class })
@PowerMockIgnore("javax.management.*")
public class SocketClientImplTest {

	private Socket socket;
	private SocketChannel socketChannel;
	private SocketClientImpl testable;

	@Before
	public void setUp() throws Exception {
		this.testable = new SocketClientImpl(new LightsHolderImpl());
		this.socketChannel = Mockito.mock(SocketChannel.class);
		Whitebox.setInternalState(this.testable, SocketChannel.class,
				this.socketChannel);
		this.socket = Mockito.mock(Socket.class);
		stubSocketChannel();
	}

	private void stubSocketChannel() throws IOException {
		PowerMockito.mockStatic(SocketChannel.class);
		Mockito.when(SocketChannel.open()).thenReturn(this.socketChannel);
		Mockito.when(this.socketChannel.isConnected()).thenReturn(true);
		Whitebox.setInternalState(this.socketChannel, "open", true);
		Mockito.when(this.socketChannel.socket()).thenReturn(this.socket);
	}

	@Test
	public void testAddPixelIntArrayIntInt() throws BoblightException,
			IOException {
		final Light light = Mockito.mock(Light.class);
		Whitebox.setInternalState(light, LightConfigMBean.class,
				Mockito.mock(LightConfigMBean.class));
		Whitebox.setInternalState(light, int[].class, new int[] { 0, 0, 0, 0 });

		Mockito.when(light.getHScanScaled()).thenReturn(new int[] { 0, 20 });
		Mockito.when(light.getVScanScaled()).thenReturn(new int[] { 0, 20 });

		this.testable.getLightsHolder().addLight(light);

		this.testable.getLightsHolder().addPixel(0, 0,
				new int[] { 255, 255, 255 });

		Assert.assertEquals(255, light.rgb[0]);
		Assert.assertEquals(255, light.rgb[1]);
		Assert.assertEquals(255, light.rgb[2]);
		Assert.assertEquals(1, light.rgb[3]);

		this.testable.sendRgb(true, null);

		this.testable.getLightsHolder().addPixel(0, 0, new int[] { 0, 0, 0 });

		Assert.assertEquals(0, light.rgb[0]);
		Assert.assertEquals(0, light.rgb[1]);
		Assert.assertEquals(0, light.rgb[2]);
		Assert.assertEquals(1, light.rgb[3]);

		this.testable.getLightsHolder().addPixel(0, 0,
				new int[] { 128, 128, 128 });

		Assert.assertEquals(128, light.rgb[0]);
		Assert.assertEquals(128, light.rgb[1]);
		Assert.assertEquals(128, light.rgb[2]);
		Assert.assertEquals(2, light.rgb[3]);
	}

	@Test
	public void testAddPixelIntIntArray() throws BoblightException, IOException {
		@SuppressWarnings("unchecked")
		final Map<String, Light> lights = Whitebox.getInternalState(
				this.testable.getLightsHolder(), Map.class);
		final Light light = Mockito.mock(Light.class);
		Whitebox.setInternalState(light, LightConfigMBean.class,
				Mockito.mock(LightConfigMBean.class));
		Whitebox.setInternalState(light, int[].class, new int[] { 0, 0, 0, 0 });
		lights.put("light1", light);

		this.testable.getLightsHolder().addPixel("light1",
				new int[] { 255, 255, 255 });

		Assert.assertEquals(255, light.rgb[0]);
		Assert.assertEquals(255, light.rgb[1]);
		Assert.assertEquals(255, light.rgb[2]);
		Assert.assertEquals(1, light.rgb[3]);

		this.testable.sendRgb(true, null);
	}

	@Test
	public void testConnect() throws BoblightException, IOException {

		this.stubSocketChannel();

		final String lightConfig = "lights 1\n"
				+ "light bottom1 scan 1 3 4 5\n";
		Mockito.when(this.socket.getInputStream()).thenReturn(
				new ByteArrayInputStream("hello\n".getBytes()),
				new ByteArrayInputStream("version 5\n".getBytes()),
				new ByteArrayInputStream(lightConfig.getBytes()));

		this.testable.connect("localhost", -1, 1000);

		Mockito.verify(this.socketChannel, Mockito.times(3)).write(
				Matchers.argThat(new ArgumentMatcher<ByteBuffer>() {

					@Override
					public boolean matches(final Object item) {
						final ByteBuffer buf = (ByteBuffer) item;
						final String string = new String(buf.array());
						if (string.equals("hello\n"))
						{
							return true;
						}
						else if (string.equals("get version\n"))
						{
							return true;
						}
						else if (string.equals("get lights\n"))
						{
							return true;
						}
						return false;
					}
				}));

	}

	@Test
	public void testDestroy() throws IOException {
		this.stubSocketChannel();
		Whitebox.setInternalState(testable, SocketChannel.class, socketChannel);
		Whitebox.setInternalState(socketChannel, "closeLock", new Object());
		Whitebox.setInternalState(socketChannel, "keyLock", new Object());

		this.testable.destroy();

		verify(this.socketChannel).close();
	}

	@Test
	public void testPing() throws BoblightException, IOException {

		this.stubSocketChannel();

		Whitebox.setInternalState(this.testable, "mSecTimeout", 100);
		Whitebox.setInternalState(this.testable, SocketChannel.class,
				this.socketChannel);

		Mockito.when(this.socket.getInputStream()).thenReturn(
				new ByteArrayInputStream("ping\n".getBytes()),
				new ByteArrayInputStream("version 5\n".getBytes()));

		this.testable.ping(null, true);
	}

	@Test
	public void testSendRgb() throws IOException, BoblightException {

		this.stubSocketChannel();

		Whitebox.setInternalState(this.testable, "mSecTimeout", 100);
		Whitebox.setInternalState(this.testable, SocketChannel.class,
				this.socketChannel);

		Light mock = mock(Light.class);
		mock.rgb = new int[] { 255, 255, 255, 0 };

		Whitebox.setInternalState(mock, LightConfigMBean.class,
				mock(LightConfigMBean.class));

		this.testable.getLightsHolder().addLight(mock);

		this.testable.sendRgb(true, null);
	}

	@Test
	public void testSetOption() throws Exception {
		// setup
		final Light mock = new Light(Mockito.mock(LightConfigMBean.class));
		this.testable.getLightsHolder().addLight(mock);

		this.testable.setOption(null, "saturation 2.0");

		Assert.assertEquals(2f, mock.getSaturation());

		this.stubSocketChannel();

		Whitebox.setInternalState(this.testable, "mSecTimeout", 100);
		Whitebox.setInternalState(this.testable, SocketChannel.class,
				this.socketChannel);

		this.testable.setOption(null, "interpolation true");

		Mockito.verify(this.socketChannel).write(
				ByteBuffer.wrap("set light null interpolation true\n"
						.getBytes()));
	}

	@Test
	public void testTrySetup() throws BoblightException, IOException {

		final ByteArrayInputStream hello = new ByteArrayInputStream(
				"hello\n".getBytes());
		final ByteArrayInputStream version = new ByteArrayInputStream(
				"version 5\n".getBytes());
		final ByteArrayInputStream lights = new ByteArrayInputStream(
				"lights 1\nlight red scan 66 66 66 66".getBytes());
		final ByteArrayInputStream light = new ByteArrayInputStream(
				"light red\nscan 66 66 66 66".getBytes());
		when(socket.getInputStream()).thenReturn(hello, version, lights, light);

		RemoteClient client = mock(RemoteClient.class);

		doThrow(new BoblightException("")).when(client).connect(anyString(),
				anyInt(), anyInt());

		PowerMockito.mockStatic(Thread.class);

		// will do a sleep of 10 secs
		this.testable.setup(Mockito.mock(FlagManager.class));

		verify(socketChannel).write(ByteBuffer.wrap("hello\n".getBytes()));
		verify(socketChannel)
				.write(ByteBuffer.wrap("get version\n".getBytes()));
		verify(socketChannel).write(ByteBuffer.wrap("get lights\n".getBytes()));

	}

}
