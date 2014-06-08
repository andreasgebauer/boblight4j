package org.boblight4j.client;

import java.awt.FileDialog;
import java.awt.TextField;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5990259603999800265L;

	public static void main(final String[] args) {
		final MainFrame mainFrame = new MainFrame();
		mainFrame.setSize(300, 200);
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		mainFrame.add(new TextField());

		new FileDialog(mainFrame);

	}

	public MainFrame() {
	}

}
