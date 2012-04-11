package org.boblight4j.client.X11;

import gnu.x11.Display;

import org.apache.log4j.Logger;
import org.boblight4j.client.ClientImpl;
import org.boblight4j.client.FlagManager;
import org.boblight4j.client.grabber.AbstractActiveGrabber;
import org.boblight4j.exception.BoblightConfigurationException;

/**
 * Base class for X11 grabber implementations.
 * 
 * @author agebauer
 * 
 */
public abstract class AbstractX11Grabber extends AbstractActiveGrabber {

	private static final Logger LOG = Logger
			.getLogger(AbstractX11Grabber.class);

	private Display display;

	private double interval;

	protected FlagManagerX11 flagManager;

	public AbstractX11Grabber(final ClientImpl client, final boolean sync,
			int width, int height, double interval) {
		super(client, sync, width, height);
		this.interval = interval;
	}

	public Display getDisplay() {
		if (this.display == null)
		{
			this.display = new Display();
		}
		return display;
	}

	@Override
	protected final int getScreenWidth() {
		return this.display.default_screen.width;
	}

	@Override
	protected final int getScreenHeight() {
		return this.display.default_screen.height;
	}

	public final void setup(final FlagManager flagManager)
			throws BoblightConfigurationException {
		LOG.debug("Setting up.");

		this.flagManager = (FlagManagerX11) flagManager;

		try
		{
			this.getDisplay();
		}
		catch (Exception e)
		{
			throw new BoblightConfigurationException(
					"Unable to get display. Ensure X11 listens on tcp.", e);
		}

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
			// TODO use interval
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

		LOG.debug("Entering extended setup");
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

}
