package org.boblight4j.client;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

import junit.framework.Assert;

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
@PrepareForTest({ Client.class, SocketChannel.class })
@PowerMockIgnore("javax.management.*")
public class ClientTest {

	private Socket socket;
	private SocketChannel socketChannel;
	private Client testable;

	@Before
	public void setUp() throws Exception {
		this.testable = new Client();
	}

	private void stubSocketChannel() throws IOException {
		PowerMockito.mockStatic(SocketChannel.class);
		this.socketChannel = Mockito.mock(SocketChannel.class);
		Mockito.when(SocketChannel.open()).thenReturn(this.socketChannel);
		Mockito.when(this.socketChannel.isConnected()).thenReturn(true);
		Whitebox.setInternalState(this.socketChannel, "open", true);
		this.socket = Mockito.mock(Socket.class);
		Mockito.when(this.socketChannel.socket()).thenReturn(this.socket);

	}

	@Test
	public void testAddPixelIntArrayIntInt() throws BoblightException {
		@SuppressWarnings("unchecked")
		final List<Light> lights = Whitebox.getInternalState(this.testable,
				List.class);
		final Light light = Mockito.mock(Light.class);
		Whitebox.setInternalState(light, LightConfigMBean.class,
				Mockito.mock(LightConfigMBean.class));
		Whitebox.setInternalState(light, int[].class, new int[] { 0, 0, 0, 0 });

		Mockito.when(light.getHScanScaled()).thenReturn(new int[] { 0, 20 });
		Mockito.when(light.getVScanScaled()).thenReturn(new int[] { 0, 20 });

		lights.add(light);

		this.testable.addPixel(0, 0, new int[] { 255, 255, 255 });

		Assert.assertEquals(255, light.rgb[0]);
		Assert.assertEquals(255, light.rgb[1]);
		Assert.assertEquals(255, light.rgb[2]);
		Assert.assertEquals(1, light.rgb[3]);
	}

	@Test
	public void testAddPixelIntIntArray() throws BoblightException {
		@SuppressWarnings("unchecked")
		final List<Light> lights = Whitebox.getInternalState(this.testable,
				List.class);
		final Light light = Mockito.mock(Light.class);
		Whitebox.setInternalState(light, LightConfigMBean.class,
				Mockito.mock(LightConfigMBean.class));
		Whitebox.setInternalState(light, int[].class, new int[] { 0, 0, 0, 0 });
		lights.add(light);

		this.testable.addPixel(0, new int[] { 255, 255, 255 });

		Assert.assertEquals(255, light.rgb[0]);
		Assert.assertEquals(255, light.rgb[1]);
		Assert.assertEquals(255, light.rgb[2]);
		Assert.assertEquals(1, light.rgb[3]);
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

		Whitebox.setInternalState(this.testable, "uSecTimeout", 100);
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

		Whitebox.setInternalState(this.testable, "uSecTimeout", 100);
		Whitebox.setInternalState(this.testable, SocketChannel.class,
				this.socketChannel);

		Light mock = mock(Light.class);
		mock.rgb = new int[] { 255, 255, 255, 0 };

		Whitebox.setInternalState(mock, LightConfigMBean.class,
				mock(LightConfigMBean.class));

		this.testable.getLights().add(mock);

		this.testable.sendRgb(true, null);
	}

	@Test
	public void testSetOption() throws Exception {
		// setup
		final Light mock = new Light(Mockito.mock(LightConfigMBean.class));
		this.testable.getLights().add(mock);

		this.testable.setOption(-1, "saturation 2.0");

		Assert.assertEquals(2f, mock.getSaturation());

		this.stubSocketChannel();

		Whitebox.setInternalState(this.testable, "uSecTimeout", 100);
		Whitebox.setInternalState(this.testable, SocketChannel.class,
				this.socketChannel);

		this.testable.setOption(-1, "interpolation true");

		Mockito.verify(this.socketChannel).write(
				ByteBuffer.wrap("set light null interpolation true\n"
						.getBytes()));
	}
}
