package org.boblight4j.jmx.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.boblight4j.jmx.client.CurveEditorPanel.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColorChoosePanel extends JPanel {

	private static final Logger LOG = LoggerFactory
			.getLogger(ColorChoosePanel.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -1663588634108832053L;

	private String color;

	private final JComboBox comboBoxColors;

	private final CurveEditorPanel curveEditor;

	private final DefaultComboBoxModel model;

	public ColorChoosePanel(final CurveEditorPanel curveEditor,
			final Object[] items) {

		this.curveEditor = curveEditor;

		this.comboBoxColors = new JComboBox();
		this.comboBoxColors.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				LOG.info("selected item: {}",
						ColorChoosePanel.this.comboBoxColors.getSelectedItem());
				final Model data = ColorChoosePanel.this.curveEditor.getData();
				ColorChoosePanel.this.color = (String) ColorChoosePanel.this.comboBoxColors
						.getSelectedItem();
				data.colorName = ColorChoosePanel.this.color;
				data.colorIndex = ColorChoosePanel.this.comboBoxColors
						.getSelectedIndex();
			}
		});

		final JLabel lblLight = new JLabel("Light");
		final GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(
				Alignment.LEADING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addComponent(lblLight, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(this.comboBoxColors, 0,
								GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		groupLayout
				.setVerticalGroup(groupLayout
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								groupLayout
										.createSequentialGroup()
										.addGroup(
												groupLayout
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																this.comboBoxColors,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(
																lblLight,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addContainerGap(276, Short.MAX_VALUE)));
		this.setLayout(groupLayout);

		this.model = new DefaultComboBoxModel(items);

		this.comboBoxColors.setModel(this.model);
	}

	public String getColor() {
		return this.color;
	}

}
