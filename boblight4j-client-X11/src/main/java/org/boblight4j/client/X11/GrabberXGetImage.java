package org.boblight4j.client.X11;

import gnu.x11.image.Image.Format;
import gnu.x11.image.ZPixmap;

import org.boblight4j.client.ClientImpl;

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

	public GrabberXGetImage(final ClientImpl boblight, final boolean sync,
			int width, int height, double interval) {
		super(boblight, sync, width, height, interval);
	}

	public int[] grabPixelAt(final int xpos, final int ypos) {
		final int rgb[] = new int[3];
		// get an image of size 1x1 at the location
		ZPixmap pixMap = (ZPixmap) this.getDisplay().default_root.image(xpos,
				ypos, 1, 1, ALL_PLANES, Format.ZPIXMAP);

		// read out pixel
		// place pixel in rgb array
		rgb[0] = pixMap.get_red(0, 0);
		rgb[1] = pixMap.get_green(0, 0);
		rgb[2] = pixMap.get_blue(0, 0);
		return rgb;
	}

	@Override
	public void cleanup() {
	}
}
