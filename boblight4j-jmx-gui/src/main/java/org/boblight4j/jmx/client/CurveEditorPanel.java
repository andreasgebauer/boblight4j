package org.boblight4j.jmx.client;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.boblight4j.server.config.IPoint;

public class CurveEditorPanel extends JPanel {

	public static class Model {
		protected int colorIndex;
		public String colorName;
		public String lightName;
		public List<org.boblight4j.server.config.IPoint> points;

		public Model() {
			this.points = new ArrayList<org.boblight4j.server.config.IPoint>();
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ChangeListener changeListener;

	private Model data = new Model();

	private org.boblight4j.server.config.Point draggedPoint;

	private boolean pointDragged;

	/**
	 * Create the panel.
	 */
	public CurveEditorPanel() {

		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				CurveEditorPanel.this.handleMouseClicked(e);
			}

			@Override
			public void mouseEntered(final MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(final MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(final MouseEvent e) {
				CurveEditorPanel.this.handleMousePressed(e);
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				CurveEditorPanel.this.handleMouseReleased(e);
			}
		});

		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(final MouseEvent e) {
				CurveEditorPanel.this.handleMouseMoved(e);
			}

			@Override
			public void mouseMoved(final MouseEvent e) {
				CurveEditorPanel.this.handleMouseMoved(e);
			}
		});
	}

	private void clamp(final Point point) {
		if (point.y < 0)
		{
			point.y = 0;
		}
		else if (point.y > this.getHeight())
		{
			point.y = this.getHeight() - 1;
		}

		if (point.x < 0)
		{
			point.x = 0;
		}
		else if (point.x > this.getWidth())
		{
			point.x = this.getWidth() - 1;
		}
	}

	public Model getData() {
		return this.data;
	}

	private org.boblight4j.server.config.Point getPoint(final int x,
			final int y, final int rad) {
		for (final IPoint point : this.getData().points)
		{
			final Point p = this.getScreenPoint(point);
			if (p.x < x + rad && p.x > x - rad)
			{
				if (p.y < y + rad && p.y > y - rad)
				{
					return (org.boblight4j.server.config.Point) point;
				}
			}
		}
		return null;
	}

	private Point getScreenPoint(final IPoint point) {
		final int x2 = (int) (point.getX() * this.getWidth());
		final int y2 = (int) (point.getY() * this.getHeight());
		return new Point(x2, y2);
	}

	protected void handleMouseClicked(final MouseEvent e) {
		final int rad = 2;
		if (this.isPoint(e.getX(), e.getY(), rad)
				&& e.getButton() == MouseEvent.BUTTON3)
		{
			this.getData().points
					.remove(this.getPoint(e.getX(), e.getY(), rad));
		}
		else if (e.getButton() == MouseEvent.BUTTON1)
		{
			final org.boblight4j.server.config.Point pFl = this.toFloatPoint(e
					.getPoint());
			this.getData().points.add(pFl);
			this.sortPoints();

			if (this.changeListener != null)
			{
				this.changeListener.stateChanged(new ChangeEvent(this.data));
			}
		}

		this.repaint();
	}

	protected void handleMouseMoved(final MouseEvent e) {
		if (this.pointDragged)
		{

			final Point point = e.getPoint();
			this.clamp(point);

			final org.boblight4j.server.config.Point floatPoint = this
					.toFloatPoint(point);
			this.draggedPoint.setLocation(floatPoint);

			this.sortPoints();

			if (this.changeListener != null)
			{
				this.changeListener.stateChanged(new ChangeEvent(this.data));
			}

			this.repaint();
		}
	}

	protected void handleMousePressed(final MouseEvent e) {
		if (this.isPoint(e.getX(), e.getY(), 3))
		{
			this.pointDragged = true;
			this.draggedPoint = this.getPoint(e.getX(), e.getY(), 3);
		}
	}

	protected void handleMouseReleased(final MouseEvent e) {
		if (this.pointDragged)
		{

			final Point point = e.getPoint();
			this.clamp(point);

			final org.boblight4j.server.config.Point floatPoint = this
					.toFloatPoint(point);
			this.draggedPoint.setLocation(floatPoint);
			this.pointDragged = false;
			this.repaint();
		}
	}

	private boolean isPoint(final int x, final int y, final int rad) {
		return this.getPoint(x, y, rad) != null;
	}

	@Override
	public void paint(final Graphics g) {
		super.paint(g);

		Point last = new Point(0, this.getHeight() - 1);

		for (final IPoint point : this.getData().points)
		{

			final Point pCnv = this.getScreenPoint(point);

			g.drawLine(last.x, last.y, pCnv.x, pCnv.y);

			g.drawRect(pCnv.x - 2, pCnv.y - 2, 4, 4);

			last = pCnv;
		}

		g.drawLine(last.x, last.y, this.getWidth() - 1, 0);

	}

	public void setData(final Model data) {
		this.data = data;
	}

	public void setListener(final ChangeListener listener) {
		this.changeListener = listener;
	}

	private void sortPoints() {
		Collections.sort(this.getData().points, new Comparator<IPoint>() {
			@Override
			public int compare(final IPoint o1, final IPoint o2) {
				if (o1.getX() > o2.getX())
				{
					return 1;
				}
				else if (o1.getX() < o2.getX())
				{
					return -1;
				}

				return 0;
			}
		});
	}

	private org.boblight4j.server.config.Point toFloatPoint(final Point point) {
		return new org.boblight4j.server.config.Point((float) point.x
				/ this.getWidth(), (float) point.y / this.getHeight());
	}
}
