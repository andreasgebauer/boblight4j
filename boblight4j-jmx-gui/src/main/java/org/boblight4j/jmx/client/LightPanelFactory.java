package org.boblight4j.jmx.client;

import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;

public final class LightPanelFactory {

	public static ColorChoosePanel createColorChooser(final Object[] items,
			final CurveEditorPanel curveEditorPanel) {
		return new ColorChoosePanel(curveEditorPanel, items);
	}

	public static CurveEditorPanel createCurvePanel(
			final ChangeListener changeListener) {
		final CurveEditorPanel curvePanel = new CurveEditorPanel();
		curvePanel.setListener(changeListener);
		curvePanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		return curvePanel;
	}

	public static JPanel createLightPanel(final CurveEditorPanel curvePanel,
			final ColorChoosePanel colorChoosePanel) {
		final JPanel lightPanel = new JPanel();
		lightPanel.setPreferredSize(new Dimension(200, 200));
		lightPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		final GroupLayout gl_contentPane = new GroupLayout(lightPanel);
		gl_contentPane.setHorizontalGroup(gl_contentPane
				.createParallelGroup(Alignment.LEADING)
				.addComponent(curvePanel, Alignment.TRAILING,
						GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
				.addComponent(colorChoosePanel, Alignment.TRAILING,
						GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(
				Alignment.LEADING).addGroup(
				gl_contentPane
						.createSequentialGroup()
						.addComponent(colorChoosePanel,
								GroupLayout.PREFERRED_SIZE, 50,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(curvePanel, GroupLayout.DEFAULT_SIZE, 50,
								Short.MAX_VALUE)));
		curvePanel.setLayout(null);
		lightPanel.setLayout(gl_contentPane);

		return lightPanel;
	}

	/**
	 * @param colors
	 *            TODO
	 * @param changeListener
	 *            TODO
	 * @param curveEditor
	 */
	public static JPanel createLightPanel(final Object[] colors,
			final ChangeListener changeListener) {

		final CurveEditorPanel curvePanel = LightPanelFactory
				.createCurvePanel(changeListener);

		final ColorChoosePanel colorChoosePanel = LightPanelFactory
				.createColorChooser(colors, curvePanel);

		return LightPanelFactory.createLightPanel(curvePanel, colorChoosePanel);
	}
}