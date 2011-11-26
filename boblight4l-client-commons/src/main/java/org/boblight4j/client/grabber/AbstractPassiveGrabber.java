package org.boblight4j.client.grabber;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.boblight4j.client.Client;

public abstract class AbstractPassiveGrabber extends AbstractGrabber implements
		PassiveGrabber {

	public AbstractPassiveGrabber(final Client client, final boolean sync,
			final int width, final int height) {
		super(client, sync, width, height);
	}

	protected void frameToBoblight(final BufferedImage img) {
		final double scaledX = (double) img.getWidth() / (double) this.width;
		final double scaledY = (double) img.getHeight() / (double) this.height;

		// read out pixels and hand them to the boblight client
		for (int y = 0; y < this.height; y++)
		{
			for (int x = 0; x < this.width; x++)
			{
				final int resX = (int) (scaledX * x + scaledX / 2);
				final int resY = (int) (scaledY * y + scaledY / 2);

				// LOG.info("x: " + resX + ", y: " + resY);
				final int rgbInt = img.getRGB(resX, resY);
				final int[] rgb = new int[3];
				final Color color = new Color(rgbInt);
				rgb[0] = color.getBlue();
				rgb[1] = color.getGreen();
				rgb[2] = color.getRed();

				// flip
				this.client.addPixel(this.width - x, y, rgb);

				// put pixel on debug image
				if (this.debug)
				{
					this.setDebugPixel(x, y, rgb);
				}
			}
		}

		// put debug image on debug window
		if (this.debug)
		{
			this.drawDebugImage();
		}
	}

}