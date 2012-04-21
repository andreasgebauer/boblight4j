package org.boblight4j.client.grabber;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.font.GraphicAttribute;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.WritableRaster;

import org.boblight4j.client.Client;
import org.boblight4j.client.FlagManager;
import org.boblight4j.exception.BoblightException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractGrabber.class })
public class AbstractGrabberTest {

	private AbstractGrabber testable;
	private Graphics canvasGraphics;
	private WritableRaster debugImgRaster;
	private int grabWidth;
	private int grabHeight;

	@Before
	public void setUp() throws Exception {
		grabWidth = 100;
		grabHeight = 80;

		testable = new AbstractGrabber(mock(Client.class), false, grabWidth,
				grabHeight) {

			@Override
			public void setup(FlagManager flagManager) throws BoblightException {
			}

			@Override
			public void cleanup() {
			}
		};

		BufferedImage image = mock(BufferedImage.class);
		PowerMockito.whenNew(BufferedImage.class)
				.withArguments(anyInt(), anyInt(), anyInt()).thenReturn(image);
		debugImgRaster = mock(WritableRaster.class);
		when(image.getRaster()).thenReturn(debugImgRaster);

		Frame frame = mock(Frame.class);
		PowerMockito.whenNew(Frame.class).withNoArguments().thenReturn(frame);

		Canvas canvas = mock(Canvas.class);
		PowerMockito.whenNew(Canvas.class).withNoArguments().thenReturn(canvas);

		when(frame.getComponents()).thenReturn(new Component[] { canvas });
		canvasGraphics = mock(Graphics.class);
		when(canvas.getGraphics()).thenReturn(canvasGraphics);
	}

	@Test
	public void testSetupDebug() throws Exception {
		testable.setupDebug();

		PowerMockito.verifyNew(Canvas.class).withNoArguments();
		PowerMockito.verifyNew(Frame.class).withNoArguments();
		PowerMockito.verifyNew(BufferedImage.class).withArguments(
				eq(grabWidth), eq(grabHeight), anyInt());
	}

	@Test
	public void testSetDebugPixel() {
		testable.setupDebug();

		testable.setDebugPixel(0, 0, new int[] { 0, 0, 0 });
	}

	@Test
	public void testDrawDebugImage() {
		testable.setupDebug();

		testable.drawDebugImage();

		verify(canvasGraphics).drawImage(any(BufferedImage.class), eq(0),
				eq(0), eq(200), eq(200), any(ImageObserver.class));
	}

	@Test
	public void testUpdateDebugFps() {
		testable.setupDebug();

		testable.updateDebugFps();
	}

}
