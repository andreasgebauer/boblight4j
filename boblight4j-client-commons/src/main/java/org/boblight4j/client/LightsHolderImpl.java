package org.boblight4j.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.boblight4j.exception.BoblightException;
import org.boblight4j.utils.MathUtils;

public class LightsHolderImpl implements LightsHolder {

	private static final int MAX_VALUE_BYTE = 255;
	private Map<String, Light> lightsMap = new HashMap<String, Light>();

	/**
	 * Sets the color of a screen pixel.
	 * 
	 * @param xPos
	 *            the x coordinate to add an rgb value to
	 * @param yPos
	 *            the x coordinate to add an rgb value to
	 * @param rgb
	 *            the color to add
	 */
	public void addPixel(final int xPos, final int yPos, final int[] rgb) {
		for (Entry<String, Light> entry : lightsMap.entrySet()) {
			Light light = entry.getValue();
			if (yPos >= light.getHScanScaled()[0]
					&& yPos <= light.getHScanScaled()[1]
					&& xPos >= light.getVScanScaled()[0]
					&& xPos <= light.getVScanScaled()[1]) {
				// any of the three color values must be greater than the
				// threshold
				if (rgb[0] >= light.getThreshold()
						|| rgb[1] >= light.getThreshold()
						|| rgb[2] >= light.getThreshold()) {
					light.rgb[0] += MathUtils.clamp(rgb[0], 0, MAX_VALUE_BYTE);
					light.rgb[1] += MathUtils.clamp(rgb[1], 0, MAX_VALUE_BYTE);
					light.rgb[2] += MathUtils.clamp(rgb[2], 0, MAX_VALUE_BYTE);
				}
				light.rgb[3]++;
			}
		}
	}

	/**
	 * Sets the color of a light.
	 * 
	 * @param lightName
	 *            the index of the light
	 * @param rgb
	 *            the RGB value
	 * @throws BoblightException
	 *             if {@link #checkLightExists(int)} throws error
	 */
	public final void addPixel(final String lightName, final int[] rgb)
			throws BoblightException {
		this.checkLightExists(lightName);

		// set to all lights
		if (lightName == null) {
			for (Entry<String, Light> entry : lightsMap.entrySet()) {
				Light light = entry.getValue();
				addPixel(rgb, light);
			}
		} else {
			final Light light = this.lightsMap.get(lightName);
			addPixel(rgb, light);
		}

	}

	private void addPixel(final int[] rgb, Light light) {
		if (rgb[0] >= light.getThreshold() || rgb[1] >= light.getThreshold()
				|| rgb[2] >= light.getThreshold()) {
			light.rgb[0] += MathUtils.clamp(rgb[0], 0, 255);
			light.rgb[1] += MathUtils.clamp(rgb[1], 0, 255);
			light.rgb[2] += MathUtils.clamp(rgb[2], 0, 255);
		}
		light.rgb[3]++;
	}

	/**
	 * Returns the light name for the integer given.
	 * 
	 * @param lightnr
	 *            the zero-based index of the light
	 * @return the light's name
	 * @throws BoblightException
	 */
	// public String getLightName(int lightnr) throws BoblightException {
	// // negative lights don't exist, so we set it to an
	// // invalid number to get the error message
	// if (lightnr < 0) {
	// lightnr = this.lightsMap.size();
	// }
	//
	// this.checkLightExists(lightnr);
	//
	// return this.lights.get(lightnr).getName();
	// }

	public Collection<Light> getLights() {
		return this.lightsMap.values();
	}

	@Override
	public void clear() {
		this.lightsMap.clear();
	}

	/**
	 * Sets the scan range for each light. Must be called before any pixel is
	 * added with addPixel.<br>
	 * <br>
	 * Calls {@link Light#calculateScaledScanRange(int, int)} for each light.
	 * 
	 * @param width
	 *            the width of the scan area
	 * @param height
	 *            the height of the scan area
	 */
	@Override
	public void setScanRange(final int width, final int height) {
		for (Entry<String, Light> entry : lightsMap.entrySet()) {
			entry.getValue().calculateScaledScanRange(width, height);
		}
	}

	/**
	 * Checks if a light with the index given exists. Throws
	 * {@link BoblightException} when the light doesn't exist.
	 * 
	 * @param lightName
	 * @throws BoblightException
	 */
	public void checkLightExists(final String lightName)
			throws BoblightException {
		if (lightName == null) {
			return;
		}
		if (this.lightsMap.get(lightName) == null) {
			throw new BoblightException("light " + lightName
					+ " doesn't exist (have these lights: "
					+ this.lightsMap.keySet() + ")");
		}
	}

	@Override
	public Light getLight(String lightnr) {
		return this.lightsMap.get(lightnr);
	}

	@Override
	public void addLight(Light light) {
		this.lightsMap.put(light.getName(), light);
	}
}
