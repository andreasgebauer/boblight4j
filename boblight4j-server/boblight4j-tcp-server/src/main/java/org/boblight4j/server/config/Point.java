package org.boblight4j.server.config;

import java.awt.geom.Point2D.Float;
import java.io.Serializable;
import java.util.ArrayList;

public class Point implements IPoint, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6680141900439796840L;

	private static Float toFloat(final IPoint point) {
		return new Float(point.getX(), point.getY());
	}

	public static Float[] toFloat(final IPoint[] array) {
		final ArrayList<Float> points = new ArrayList<Float>();
		for (final IPoint point : array)
		{
			points.add(Point.toFloat(point));
		}
		return points.toArray(new Float[] {});
	}

	private float x;

	private float y;

	public Point() {
	}

	public Point(final float f, final float g) {
		this.x = f;
		this.y = g;
	}

	public Point(final Float float1) {
		this.x = float1.x;
		this.y = float1.y;
	}

	@Override
	public float getX() {
		return this.x;
	}

	@Override
	public float getY() {
		return this.y;
	}

	public void setLocation(final org.boblight4j.server.config.Point floatPoint) {
		this.x = floatPoint.x;
		this.y = floatPoint.y;
	}

}
