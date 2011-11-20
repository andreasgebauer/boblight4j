package org.boblight4j.client.X11;

import gnu.x11.image.Image.Format;
import gnu.x11.image.ZPixmap;

import java.io.IOException;

import org.boblight4j.client.Client;
import org.boblight4j.exception.BoblightException;

/**
 * This class is the fast X11 grabber version.
 * 
 * clients/boblight-X11/grabber-xgetimage.cpp
 * 
 * @author agebauer
 * 
 */
public class GrabberXGetImage extends AbstractX11Grabber {

	private static final int ALL_PLANES = 0x00ffffff;

	public GrabberXGetImage(final Client boblight, final boolean stop,
			final boolean sync) {
		super(boblight, stop, sync);
	}

	@Override
	public void run() {
		ZPixmap pixMap;
		final int rgb[] = new int[3];

		this.getClient().setScanRange(this.size, this.size);

		while (!this.stop)
		{
			this.updateDimensions();

			for (int y = 0; y < this.size && !this.stop; y++)
			{
				for (int x = 0; x < this.size && !this.stop; x++)
				{
					// position of pixel to capture
					final double colWidth = this.display.default_screen.width
							/ this.size;
					final double rowHeight = this.display.default_screen.height
							/ this.size;

					final int xpos = (int) (x * colWidth + colWidth / 2);
					final int ypos = (int) (y * rowHeight + rowHeight / 2);

					// get an image of size 1x1 at the location
					pixMap = (ZPixmap) this.display.default_root.image(xpos,
							ypos, 1, 1, ALL_PLANES, Format.ZPIXMAP);

					// read out pixel
					// place pixel in rgb array
					rgb[0] = pixMap.get_red(0, 0);
					rgb[1] = pixMap.get_green(0, 0);
					rgb[2] = pixMap.get_blue(0, 0);

					// add pixel to boblight
					this.getClient().addPixel(x, y, rgb);

					// put pixel on debug image
					if (this.isDebug())
					{
						this.setDebugPixel(x, y, rgb);
					}
				}
			}

			// send rgb values to boblightd
			try
			{
				this.getClient().sendRgb(this.sync, null);
			}
			catch (final IOException e)
			{
				// recoverable error
				return;
			}
			catch (final BoblightException e)
			{
				// recoverable error
				return;
			}

			// put debug image on debug window
			if (this.isDebug())
			{
				super.drawDebugImage();

			}
			this.updateDebugFps();
		}

	}
}
