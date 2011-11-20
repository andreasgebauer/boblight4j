package org.boblight4j.client.X11;

import gnu.x11.Display;
import gnu.x11.Window;

import java.awt.Canvas;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.util.Timer;

import org.apache.log4j.Logger;
import org.boblight4j.client.Client;
import org.boblight4j.exception.BoblightConfigurationException;

public abstract class AbstractX11Grabber {

	private static final Logger LOG = Logger
			.getLogger(AbstractX11Grabber.class);

	private int debugWindowHeight;
	private int debugWindowWidth;

	protected final boolean stop;
	protected final boolean sync;
	protected Frame frame;
	protected Display display;
	protected Window rootWin;
	protected int size;

	private BufferedImage debugImg;
	private final Client client;
	private boolean debug;
	private double interval;
	private long lastMeasurement;
	private long lastUpdate;
	private long measurements;
	private int nrMeasurements;
	private Timer timer;

	public AbstractX11Grabber(final Client client, final boolean stop,
			final boolean sync) {
		this.client = client;
		this.stop = stop;
		this.sync = sync;
	}

	/**
	 * Overridable method for subclasses to do extended setup.
	 * 
	 * @return
	 */
	protected void extendedSetup() throws BoblightConfigurationException {
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

	protected Client getClient() {
		return this.client;
	}

	public boolean isDebug() {
		return debug;
	}

	public BufferedImage getDebugImg() {
		return debugImg;
	}

	protected void setDebugPixel(int x, int y, int[] rgb) {
		this.debugImg.getRaster().setPixel(x, y, rgb);
	}

	public void drawDebugImage() {
		final Canvas component = (Canvas) this.frame.getComponents()[0];
		component.getGraphics().drawImage(this.getDebugImg(), 0, 0,
				this.debugWindowWidth, this.debugWindowHeight, null);
	}

	public void setup() throws BoblightConfigurationException {
		this.display = new Display();
		if (this.display == null)
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

		this.rootWin = this.display.default_root;
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

	void updateDebugFps() {
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

	protected void updateDimensions() {
	}

	public boolean Wait() {
		if (this.interval > 0.0) // wait for timer
		{
			synchronized (this.timer)
			{
				try
				{
					this.timer.wait();
				}
				catch (final InterruptedException e)
				{
					LOG.warn("Error during Object.wait().", e);
				}
			}
		}
		// #ifdef HAVE_LIBGL
		// else //interval is negative, wait for vblanks
		// {
		// if (!m_vblanksignal.Wait(Round32(m_interval * -1.0)))
		// {
		// m_error = m_vblanksignal.GetError();
		// return false; //unrecoverable error
		// }
		// }
		// #endif

		return true;
	}

	/**
	 * Starts the processing.
	 */
	public abstract void run();

}
