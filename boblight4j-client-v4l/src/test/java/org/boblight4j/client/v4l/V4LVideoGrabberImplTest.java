package org.boblight4j.client.v4l;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

import org.boblight4j.client.LightsHolder;
import org.boblight4j.client.SocketClientImpl;
import org.boblight4j.exception.BoblightException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import au.edu.jcu.v4l4j.FrameGrabber;
import au.edu.jcu.v4l4j.RawFrameGrabber;
import au.edu.jcu.v4l4j.RawFrameGrabberExt;
import au.edu.jcu.v4l4j.V4L4JRaster;
import au.edu.jcu.v4l4j.VideoDevice;
import au.edu.jcu.v4l4j.VideoFrame;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ V4LImageGrabberImpl.class, VideoDevice.class,
		RawFrameGrabber.class, V4L4JRaster.class })
@SuppressStaticInitializationFor({ "au.edu.jcu.v4l4j.VideoDevice",
		"au.edu.jcu.v4l4j.AbstractGrabber" })
public class V4LVideoGrabberImplTest {

	private V4LImageGrabberImpl testable;

	private SocketClientImpl client;
	private RawFrameGrabber grabber;

	private static int width = 32;

	private static int height = 24;

	private LightsHolder lightsHolder;

	@Before
	public void setUpClass() throws Exception {
		lightsHolder = mock(LightsHolder.class);

		client = mock(SocketClientImpl.class);
		when(client.getLightsHolder()).thenReturn(lightsHolder);

		testable = new V4LImageGrabberImpl(client, false, width, height);

		final VideoDevice vd = Mockito.mock(VideoDevice.class);
		PowerMockito.whenNew(VideoDevice.class)
				.withParameterTypes(String.class)
				.withArguments(Matchers.anyString()).thenReturn(vd);

		grabber = PowerMockito.mock(RawFrameGrabberExt.class);

		PowerMockito.when(
				vd.getRawFrameGrabber(Matchers.anyInt(), Matchers.anyInt(),
						Matchers.anyInt(), Matchers.anyInt())).thenReturn(
				grabber);

	}

	@Test
	public void testSetup() throws BoblightException, V4L4JException {
		FlagManagerV4l flagManager = mock(FlagManagerV4l.class);
		flagManager.device = "/dev/video1";

		testable.setup(flagManager);

		Mockito.verify(grabber).startCapture();
	}

	@Test
	public void testNextFrame() throws Exception {
		final VideoFrame frame = Mockito.mock(VideoFrame.class);
		Mockito.when(frame.getCaptureTime()).thenAnswer(new Answer<Long>() {

			@Override
			public Long answer(final InvocationOnMock invocation)
					throws Throwable {
				return System.currentTimeMillis() * 1000;
			}
		});

		FrameGrabber frameGrabber = Mockito.mock(FrameGrabber.class);
		when(frameGrabber.getWidth()).thenReturn(320);
		when(frameGrabber.getHeight()).thenReturn(240);
		Whitebox.setInternalState(testable, FrameGrabber.class, frameGrabber);

		V4L4JRaster raster = mock(V4L4JRaster.class);
		PowerMockito
				.whenNew(V4L4JRaster.class)
				.withParameterTypes(SampleModel.class, DataBuffer.class,
						Point.class).withArguments(any(), any(), any())
				.thenReturn(raster);
		BufferedImage image = mock(BufferedImage.class);
		PowerMockito
				.whenNew(BufferedImage.class)
				.withParameterTypes(ColorModel.class, WritableRaster.class,
						boolean.class, Hashtable.class)
				.withArguments(any(), any(), any(), any()).thenReturn(image);

		testable.nextFrame(frame);

		verify(lightsHolder, times(width * height)).addPixel(anyInt(),
				anyInt(), any(int[].class));
	}
}
