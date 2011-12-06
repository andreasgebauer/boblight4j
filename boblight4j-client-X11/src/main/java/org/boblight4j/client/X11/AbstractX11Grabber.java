package org.boblight4j.client.X11;

import gnu.x11.Display;

import org.apache.log4j.Logger;
import org.boblight4j.client.AbstractFlagManager;
import org.boblight4j.client.Client;
import org.boblight4j.client.grabber.AbstractActiveGrabber;
import org.boblight4j.exception.BoblightConfigurationException;

public abstract class AbstractX11Grabber extends AbstractActiveGrabber {

	private static final Logger LOG = Logger
			.getLogger(AbstractX11Grabber.class);

	private Display display;

	private double interval;

	public AbstractX11Grabber(final Client client, final boolean sync,
			int size, double interval) {
		super(client, sync, size, size);
		this.interval = interval;
	}

	public Display getDisplay() {
		return display;
	}

	@Override
	protected int getScreenWidth() {
		return this.display.default_screen.width;
	}

	@Override
	protected int getScreenHeight() {
		return this.display.default_screen.height;
	}

	public void setup(final AbstractFlagManager flagManager)
			throws BoblightConfigurationException {
		LOG.debug("Setting up.");

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
