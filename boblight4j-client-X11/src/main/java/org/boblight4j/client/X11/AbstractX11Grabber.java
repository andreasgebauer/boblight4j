package org.boblight4j.client.X11;

import gnu.x11.Display;

import java.awt.Canvas;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.boblight4j.client.Client;
import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightException;

public abstract class AbstractX11Grabber {

	private static final Logger LOG = Logger
			.getLogger(AbstractX11Grabber.class);

	private final boolean sync;
	private final Client client;

	private int debugWindowHeight;
	private int debugWindowWidth;

	private Frame frame;
	private Display display;
	private int size;
	private boolean stop;

	private BufferedImage debugImg;
	private boolean debug;
	private double interval;
	private long lastMeasurement;
	private long lastUpdate;
	private long measurements;
	private int nrMeasurements;

	public AbstractX11Grabber(final Client client, final boolean sync) {
		this.client = client;
		this.sync = sync;
	}

	public void setDebug(final String debugdpy) {

		this.debugWindowWidth = Math.max(200, this.size);
		this.debugWindowHeight = Math.max(200, this.size);

		this.frame = new Frame();
		this.frame.add(new Canvas());

		this.debugImg = new BufferedImage(this.size, this.size,
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

	public void setInterval(final double interval) {
		this.interval = interval;
	}

	public void setSize(final int pixels) {
		this.size = pixels;
	}

	public Display getDisplay() {
		return display;
	}

	public boolean isDebug() {
		return debug;
	}

	public BufferedImage getDebugImg() {
		return debugImg;
	}

	private void setDebugPixel(int x, int y, int[] rgb) {
		this.debugImg.getRaster().setPixel(x, y, rgb);
	}

	public void drawDebugImage() {
		final Canvas component = (Canvas) this.frame.getComponents()[0];
		component.getGraphics().drawImage(this.getDebugImg(), 0, 0,
				this.debugWindowWidth, this.debugWindowHeight, null);
	}

	public void setup() throws BoblightConfigurationException {
		this.display = new Display();
		if (this.getDisplay() == null)
		{
			String error = "unable to open display";
			if (gnu.util.Environment.value("DISPLAY") != null)
			{
				error += " " + gnu.util.Environment.value("DISPLAY");
			}
			else
			{
				error += ", DISPLAY environment variable not set";
			}
			throw new BoblightConfigurationException(error);
		}

		this.updateDimensions();

		if (this.interval > 0.0) // set up timer
		{
			// m_timer.SetInterval(Round64(m_interval * 1000000.0));
		}
		// #ifdef HAVE_LIBGL
		// else //interval is negative so sync to vblank instead
		// {
		// if (!m_vblanksignal.Setup())
		// {
		// m_error = m_vblanksignal.GetError();
		// return false; //unrecoverable error
		// }
		// }
		// #endif

		this.extendedSetup(); // run stuff from derived classes
	}

	/**
	 * Overridable method for subclasses to do extended setup.
	 * 
	 * @return
	 */
	protected void extendedSetup() throws BoblightConfigurationException {
	}

	/**
	 * Overridable
	 */
	protected void updateDimensions() {
	}

	private void updateDebugFps() {
		if (this.isDebug())
		{
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
				if (this.nrMeasurements > 0)
				{
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

	/**
	 * Starts the processing.
	 */
	public final void run() {
		this.client.setScanRange(this.size, this.size);

		while (!this.stop)
		{
			this.updateDimensions();

			grabPixels(this.size, this.size,
					this.getDisplay().default_screen.width,
					this.getDisplay().default_screen.height);

			// send rgb values to boblightd
			try
			{
				this.client.sendRgb(this.sync, null);
			}
			catch (final IOException e)
			{
				// recoverable error
				return;
			}
			catch (final BoblightException e)
			{
				// recoverable error
				return;
			}

			// put debug image on debug window
			if (this.isDebug())
			{
				this.drawDebugImage();

			}
			this.updateDebugFps();
		}

	}

	private void grabPixels(int height, int width, int screenWidth,
			int screenHeight) {

		for (int y = 0; y < height && !this.stop; y++)
		{
			for (int x = 0; x < width && !this.stop; x++)
			{
				// position of pixel to capture
				final double colWidth = (double) screenWidth / (double) width;
				final double rowHeight = (double) screenHeight
						/ (double) height;

				final int xpos = (int) (x * colWidth + colWidth / 2);
				final int ypos = (int) (y * rowHeight + rowHeight / 2);

				final int rgb[] = grabPixelAt(xpos, ypos);

				// add pixel to boblight
				this.client.addPixel(x, y, rgb);

				// put pixel on debug image
				if (this.isDebug())
				{
					this.setDebugPixel(x, y, rgb);
				}
			}
		}
	}

	protected abstract int[] grabPixelAt(int xpos, int ypos);

}
