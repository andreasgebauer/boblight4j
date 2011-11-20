package org.boblight4j.server.config;

import java.util.Map;

public interface LightAccessorMBean {

	float[] getAdjust();

	float[] getBlacklevel();

	/**
	 * Returns color adjustments for each color.
	 * 
	 * @return
	 */
	Map<String, IPoint[]> getColorAdjustments();

	float[] getGamma();

	float getHscanEnd();

	float getHscanStart();

	int getNrUsers();

	float[] getRgb();

	float getSpeed();

	long getTime();

	float[] getVscan();

	void setColorAdjustment(int colorNr, float adjust);

	void setColorAdjustments(int colorNr, IPoint[] array);

	void setHscanEnd(float hScanEnd);

	void setHscanStart(float hScanStart);

	void setSpeed(float speed);
}
