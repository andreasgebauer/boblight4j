package org.boblight4j.client;

import org.boblight4j.client.mbean.LightConfigMBean;

public class LightConfig implements LightConfigMBean {

	// affected by getRgb method //
	// considered to be global
	private float autospeed = -1;
	private final int hScanScaled[] = new int[2];

	private float satRangeEnd = -1;
	private float satRangeStart = -1;
	private float saturation = -1;
	private float singleChange = -1;

	// affected by addPixel method //
	private int threshold = -1;
	private boolean use = false;

	private float value = -1;
	private float valueRangeEnd = -1;
	private float valueRangeStart = -1;
	private int vScanScaled[] = new int[2];

	@Override
	public float getAutospeed() {
		return this.autospeed;
	}

	@Override
	public int[] getHScanScaled() {
		return this.hScanScaled.clone();
	}

	@Override
	public float getSatRangeEnd() {
		return this.satRangeEnd;
	}

	@Override
	public float getSatRangeStart() {
		return this.satRangeStart;
	}

	@Override
	public float getSaturation() {
		return this.saturation;
	}

	@Override
	public float getSinglechange() {
		return this.singleChange;
	}

	@Override
	public int getThreshold() {
		return this.threshold;
	}

	@Override
	public float getValue() {
		return this.value;
	}

	@Override
	public float getValueRangeEnd() {
		return this.valueRangeEnd;
	}

	@Override
	public float getValueRangeStart() {
		return this.valueRangeStart;
	}

	@Override
	public int[] getVScanScaled() {
		return this.vScanScaled.clone();
	}

	@Override
	public boolean isUse() {
		return this.use;
	}

	@Override
	public void setAutospeed(final float autospeed) {
		this.autospeed = autospeed;
	}

	@Override
	public void setHscanScaledEnd(final int i) {
		this.hScanScaled[1] = i;
	}

	@Override
	public void setHscanScaledStart(final int i) {
		this.hScanScaled[0] = i;
	}

	@Override
	public void setSatRangeEnd(final float satrange) {
		this.satRangeEnd = satrange;
	}

	@Override
	public void setSatRangeStart(final float satrange) {
		this.satRangeStart = satrange;
	}

	@Override
	public void setSaturation(final float saturation) {
		this.saturation = saturation;
	}

	@Override
	public void setSingleChange(final float singlechange) {
		this.singleChange = singlechange;
	}

	@Override
	public void setThreshold(final int threshold) {
		this.threshold = threshold;
	}

	@Override
	public void setUse(final boolean use) {
		this.use = use;
	}

	@Override
	public void setValue(final float value) {
		this.value = value;
	}

	@Override
	public void setValueRangeEnd(final float valueRangeEnd) {
		this.valueRangeEnd = valueRangeEnd;
	}

	@Override
	public void setValueRangeStart(final float valueRangeStart) {
		this.valueRangeStart = valueRangeStart;
	}

	@Override
	public void setVScanScaled(final int[] vscanscaled) {
		this.vScanScaled = vscanscaled.clone();
	}

}
