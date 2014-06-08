package org.boblight4j.client;

import java.util.Collection;

import org.boblight4j.exception.BoblightException;

public interface LightsHolder {

	void addPixel(int x, int y, int[] rgb);

	void addPixel(String i, int[] rgb) throws BoblightException;

	void clear();

	void addLight(Light light);

	Light getLight(String lightnr);

	Collection<Light> getLights();

	void checkLightExists(String lightnr) throws BoblightException;

	void setScanRange(final int width, final int height);

}
