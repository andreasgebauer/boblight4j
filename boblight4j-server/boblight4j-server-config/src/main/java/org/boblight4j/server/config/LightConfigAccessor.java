package org.boblight4j.server.config;

import java.awt.geom.Point2D.Float;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LightConfigAccessor implements LightConfigAccessorMBean {

	private final LightConfig light;

	public LightConfigAccessor(final LightConfig light) {
		this.light = light;
	}

	@Override
	public float[] getAdjust() {
		final float[] value = new float[this.light.getNrColors()];
		for (int i = 0; i < this.light.getNrColors(); i++) {
			value[i] = this.light.getAdjust(i);
		}
		return value;
	}

	@Override
	public float[] getBlacklevel() {
		final float[] value = new float[this.light.getNrColors()];
		for (int i = 0; i < this.light.getNrColors(); i++) {
			value[i] = this.light.getBlacklevel(i);
		}
		return value;
	}

	@Override
	public Map<String, IPoint[]> getColorAdjustments() {
		final Map<String, IPoint[]> value = new HashMap<String, IPoint[]>();

		final Collection<ColorConfig> colors = this.light.getColors();

		int i = 0;
		for (final Iterator<ColorConfig> iterator = colors.iterator(); iterator
				.hasNext(); i++) {
			final ColorConfig color = iterator.next();

			final List<Point> points = new ArrayList<Point>();
			final Float[] adjusts = this.light.getAdjusts(i);
			for (int j = 0; adjusts != null && j < adjusts.length; j++) {
				points.add(new Point(adjusts[j]));
			}

			final Point[] array = points.toArray(new Point[points.size()]);
			value.put(color.getName(), array);
		}

		return value;
	}

	@Override
	public float[] getGamma() {
		final float[] value = new float[this.light.getNrColors()];
		for (int i = 0; i < this.light.getNrColors(); i++) {
			value[i] = this.light.getGamma(i);
		}
		return value;
	}

	@Override
	public float getHscanEnd() {
		return this.light.getHscan()[1];
	}

	@Override
	public float getHscanStart() {
		return this.light.getHscan()[0];
	}

	@Override
	public float[] getVscan() {
		return this.light.getVscan();
	}

	@Override
	public void setColorAdjustment(final int colorNr, final float adjust) {
		this.light.setAdjust(colorNr, adjust);
	}

	@Override
	public void setColorAdjustments(final int colorNr, final IPoint[] array) {
		this.light.setAdjusts(colorNr, Point.toFloat(array));
	}

	@Override
	public void setHscanEnd(final float hScanEnd) {
		this.light.getHscan()[1] = hScanEnd;
	}

	@Override
	public void setHscanStart(final float hScanStart) {
		this.light.getHscan()[0] = hScanStart;
	}

}
