package org.boblight4j.client.grabber;

import java.awt.Canvas;
import java.awt.Frame;
import java.awt.image.BufferedImage;

import org.boblight4j.client.Client;

/**
 * This is base class for a grabber implementation which grabs pixels from a
 * device such as a screen or a video input device.<br>
 * There are abstract active and passive grabber implementations. The active
 * grabber acts actively by grabbing pixels periodically.
 * 
 * @author agebauer
 * 
 */
public abstract class AbstractGrabber implements Grabber {

	protected final Client client;
	protected final boolean sync;
	protected final int width;
	protected final int height;

	boolean debug;

	private int debugWindowHeight;
	private int debugWindowWidth;
	private Frame frame;
	private BufferedImage debugImg;

	private long lastMeasurement;
	private long lastUpdate;
	private long measurements;
	private int nrMeasurements;

	public AbstractGrabber(Client client2, boolean sync, int width,
			int height) {
		this.client = client2;
		this.sync = sync;
		this.width = width;
		this.height = height;
	}

	protected void setDebugPixel(final int x, final int y, final int[] rgb) {
		this.debugImg.getRaster().setPixel(x, y, rgb);
	}

	/**
	 * Initialises the debug window.
	 */
	@Override
	public void setupDebug() {

		this.debugWindowWidth = Math.max(200, this.width);
		this.debugWindowHeight = Math.max(200, this.height);

		this.frame = new Frame();
		this.frame.add(new Canvas());

		this.debugImg = new BufferedImage(this.width, this.height,
				BufferedImage.TYPE_INT_RGB);

		this.frame.setSize(this.debugWindowWidth, this.debugWindowHeight);
		this.frame.setVisible(true);

		// set up stuff for measuring fps
		this.lastUpdate = System.currentTimeMillis();
		this.lastMeasurement = this.lastUpdate;
		this.measurements = 0;
		this.nrMeasurements = 0;

		this.debug = true;
	}

	public void drawDebugImage() {
		final Canvas component = (Canvas) this.frame.getComponents()[0];
		component.getGraphics().drawImage(this.debugImg, 0, 0,
				this.debugWindowWidth, this.debugWindowHeight, null);
	}

	protected void updateDebugFps() {
		if (this.debug) {
			final long now = System.currentTimeMillis(); // current timestamp
			final long timeDiffMsr = now - this.lastMeasurement;
			this.measurements += timeDiffMsr;
			// diff between last time we were here
			this.nrMeasurements++; // got another measurement
			this.lastMeasurement = now; // save the timestamp

			if (now - this.lastUpdate >= 1000) // if we've measured for one
												// second,
			// place fps on debug window title
			{
				this.lastUpdate = now;

				double fps = 0.0;
				if (this.nrMeasurements > 0) {
					// we need at least one measurement
					fps = 1.0 / ((float) this.measurements / (float) this.nrMeasurements) * 1000;
				}
				this.measurements = 0;
				this.nrMeasurements = 0;

				final String strfps = fps + " fps";

				this.frame.setTitle(strfps);
			}
		}
	}

}
