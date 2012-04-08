package org.boblight4j.client.v4l;

import static org.mockito.Mockito.mock;

import org.boblight4j.client.ClientImpl;
import org.boblight4j.exception.BoblightException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import au.edu.jcu.v4l4j.RawFrameGrabber;
import au.edu.jcu.v4l4j.VideoDevice;
import au.edu.jcu.v4l4j.VideoFrame;

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { V4LImageGrabberImpl.class, VideoDevice.class,
		RawFrameGrabber.class })
@PrepareOnlyThisForTest(fullyQualifiedNames = { "au.edu.jcu.v4l4j.AbstractGrabber" })
@SuppressStaticInitializationFor({ "au.edu.jcu.v4l4j.VideoDevice" })
// TODO make fit for test
@Ignore
public class V4LVideoGrabberImplTest {

	private static V4LImageGrabberImpl testable;

	private static ClientImpl client;

	@BeforeClass
	public static void setUpClass() throws Exception {
		client = mock(ClientImpl.class);

		testable = new V4LImageGrabberImpl(client, false, 0, 0);
	}

	@Mock
	private FlagManagerV4l flagManager;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		final VideoDevice vd = Mockito.mock(VideoDevice.class);
		PowerMockito.whenNew(VideoDevice.class)
				.withParameterTypes(String.class)
				.withArguments(Matchers.anyString()).thenReturn(vd);

		final RawFrameGrabber fg = PowerMockito.mock(RawFrameGrabber.class);

		// PowerMockito
		// .whenNew(RawFrameGrabber.class)
		// .withParameterTypes(DeviceInfo.class, long.class, int.class,
		// int.class, int.class, int.class, Tuner.class,
		// ImageFormat.class)
		// .withArguments(Matchers.any(DeviceInfo.class),
		// Matchers.anyLong(), Matchers.anyInt(),
		// Matchers.anyInt(), Matchers.anyInt(),
		// Matchers.anyInt(), Matchers.any(Tuner.class),
		// Matchers.any(ImageFormat.class)).thenReturn(fg);

		PowerMockito.when(
				vd.getRawFrameGrabber(Matchers.anyInt(), Matchers.anyInt(),
						Matchers.anyInt(), Matchers.anyInt())).thenReturn(fg);

		final int width = 64;

		PowerMockito
				.doReturn(width)
				.when(fg,
						WhiteboxImpl.getMethod(RawFrameGrabber.class,
								"getWidth")).withNoArguments();

		// PowerMockito.when(fg.getWidth()).thenReturn(width);
		// PowerMockito.when(fg.getHeight()).thenReturn(width);

		this.flagManager.device = "/dev/video1";
	}

	@Test
	public void testNextFrame() {
		final VideoFrame frame = Mockito.mock(VideoFrame.class);
		Mockito.when(frame.getCaptureTime()).thenAnswer(new Answer<Long>() {

			@Override
			public Long answer(final InvocationOnMock invocation)
					throws Throwable {
				return System.currentTimeMillis() * 1000;
			}
		});

		testable.nextFrame(frame);
	}

	@Test
	public void testSetup() throws BoblightException {
		testable.setup(this.flagManager);
	}

}
