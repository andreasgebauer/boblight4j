package org.boblight4j.server.config;

import java.awt.geom.Point2D.Float;

public class ColorLightAdjust {

	private float adjust;
	private Float[] adjusts;

	public ColorLightAdjust() {
		this.adjust = 1.0f;
	}

	public float getAdjust() {
		return this.adjust;
	}

	public Float[] getAdjusts() {
		return this.adjusts;
	}

	public void setAdjust(final float adjust) {
		this.adjust = adjust;
	}

	public void setAdjusts(final Float[] adjusts) {
		this.adjusts = adjusts;
	}

}
