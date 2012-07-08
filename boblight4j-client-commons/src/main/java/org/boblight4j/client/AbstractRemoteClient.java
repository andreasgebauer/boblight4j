package org.boblight4j.client;

import java.util.Collection;

import org.boblight4j.client.mbean.LightConfigMBean;
import org.boblight4j.exception.BoblightCommunicationException;
import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightException;
import org.boblight4j.exception.BoblightParseException;
import org.boblight4j.utils.MBeanUtils;
import org.boblight4j.utils.Message;
import org.boblight4j.utils.Misc;
import org.boblight4j.utils.StdIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRemoteClient implements RemoteClient {

	private static final Logger LOG = LoggerFactory
			.getLogger(AbstractRemoteClient.class);

	private final LightsHolder lightsHolder;

	public AbstractRemoteClient(LightsHolder lightsHolder) {
		this.lightsHolder = lightsHolder;
	}

	/**
	 * Parses the response message to the get lights request and adds all the
	 * lights in the message. Removes any light currently set.
	 * 
	 * @param message
	 *            the message to parse
	 * @throws BoblightException
	 *             in case of parsing fails
	 */
	protected void parseLights(Message message) throws BoblightException {
		LOG.debug("Parsing lights response message: "
				+ message.message.toString() + ".");
		this.lightsHolder.clear();

		String word;
		int nrlights = 0;

		// first word in the message is "lights", second word is the number of
		// lights
		if (!this.parseWord(message, "lights")
				|| (word = Misc.getWord(message.message)) == null) {
			throw new BoblightCommunicationException(
					"Unable to parse number of lights.");
		}

		try {
			nrlights = Integer.parseInt(word);
			if (nrlights < 1) {
				throw new BoblightConfigurationException(
						"Number of lights is negative:" + nrlights
								+ ". Message: " + message.message.toString());
			}
		} catch (final NumberFormatException e) {
			throw new BoblightParseException(
					"Number of lights is unparseable.", e);
		}

		// create a global light MBean and register it
		final LightConfigMBean gblCfg = new LightConfig();
		MBeanUtils.registerBean("org.boblight4j.client:type=GlobalLightConfig",
				gblCfg);

		for (int i = 0; i < nrlights; i++) {
			final Light light = new Light(gblCfg);

			message = nextMessage();

			// first word sent is "light, second one is the name
			String lightName;
			if (!this.parseWord(message, "light")
					|| (lightName = Misc.getWord(message.message)) == null) {
				throw new BoblightParseException(
						"Cannot parse light name. Received following message: "
								+ message.message.toString());
			}
			light.setName(lightName);

			MBeanUtils.registerBean("org.boblight4j.client:type=LightConfig ["
					+ light.getName(), light);

			// third one is "scan"
			if (!this.parseWord(message, "scan")) {
				throw new BoblightParseException(
						"Cannot parse light scan range. Identifier 'scan' wasn't found.");
			}

			// now we read the scanrange
			final StringBuilder scanarea = new StringBuilder("");
			for (int j = 0; j < 4; j++) {
				if ((word = Misc.getWord(message.message)) == null) {
					throw new BoblightParseException("Cannot parse scan range.");
				}
				scanarea.append(word).append(' ');
			}

			// ConvertFloatLocale(scanarea); // workaround for locale mismatch
			// (,
			// and .)

			Object[] rs = null;
			try {
				if ((rs = StdIO.sscanf(scanarea.toString(), "%f %f %f %f")).length != 4) {
					throw new BoblightParseException("Cannot parse scan range.");
				}
			} catch (BoblightParseException e) {
				throw new BoblightParseException("Cannot parse scan range.", e);
			}

			light.setHscan((Float) rs[0], (Float) rs[1]);
			light.setVscan((Float) rs[2], (Float) rs[3]);

			this.lightsHolder.addLight(light);
		}
	}

	protected boolean parseWord(final Message message, final String wordtocmp) {
		String readword;
		try {
			readword = Misc.getWord(message.message);
			if (!readword.equals(wordtocmp)) {
				return false;
			}
		} catch (BoblightParseException e) {
			return false;
		}

		return true;
	}

	public void setOption(final String lightnr, final String option)
			throws BoblightException {
		this.lightsHolder.checkLightExists(lightnr);

		if (lightnr == null) {
			final Collection<Light> lights = this.lightsHolder.getLights();
			for (Light light : lights) {
				if (light.setOption(option)) {
					sendOption(light, option);
				}
			}
		} else {
			this.lightsHolder.getLight(lightnr);
			final Light light = this.lightsHolder.getLight(lightnr);
			if (light.setOption(option)) {
				sendOption(light, option);
			}
		}
	}

	@Override
	public LightsHolder getLightsHolder() {
		return this.lightsHolder;
	}

	public abstract void ping(Object object, boolean b)
			throws BoblightException;

}
