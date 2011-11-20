package org.boblight4j.client;

public interface LightConfigMBean {

	float getAutospeed();

	int[] getHScanScaled();

	float getSatRangeEnd();

	float getSatRangeStart();

	float getSaturation();

	float getSinglechange();

	int getThreshold();

	float getValue();

	float getValueRangeEnd();

	float getValueRangeStart();

	int[] getVScanScaled();

	boolean isUse();

	void setAutospeed(float autospeed);

	void setHscanScaledEnd(int i);

	void setHscanScaledStart(int i);

	void setSatRangeEnd(float satrange);

	void setSatRangeStart(float satrange);

	void setSaturation(float saturation);

	void setSingleChange(float singlechange);

	void setThreshold(int threshold);

	void setUse(boolean use);

	void setValue(float value);

	void setValueRangeEnd(float valueRangeEnd);

	void setValueRangeStart(float valueRangeStart);

	void setVScanScaled(int[] vscanscaled);

}
