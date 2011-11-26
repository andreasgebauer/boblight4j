package org.boblight4j.client.X11;

import org.boblight4j.client.Client;

/**
 * clients/boblight-X11/grabber-xrender.cpp
 * 
 * @author agebauer
 * 
 */
public class GrabberXRender extends AbstractX11Grabber {

	public GrabberXRender(final Client boblight, final boolean sync,
			final int size, double interval) {
		super(boblight, sync, size, interval);
	}

	// @Override
	// public void run() {

	// this.getClient().setScanRange(this.size, this.size);

	// while (!this.stop)
	// {
	// this.updateDimensions();
	//
	// throw new UnsupportedOperationException(
	// "XRender is currently unsupported.");

	// //we want to scale the root window to the pixmap
	// m_transform.matrix[0][0] = m_rootwin.width;
	// m_transform.matrix[1][1] = m_rootwin.height;
	// m_transform.matrix[2][2] = m_size;
	//
	// XRenderSetPictureTransform (m_dpy, m_srcpicture, &m_transform);
	//
	// //render the thing
	// XRenderComposite(m_dpy, PictOpSrc, m_srcpicture, None,
	// m_dstpicture, 0, 0, 0, 0, 0, 0, m_size, m_size);
	// XSync(m_dpy, False);
	//
	// //copy pixmap to the ximage in shared mem
	// XShmGetImage(m_dpy, m_pixmap, m_xim, 0, 0, AllPlanes);
	//
	// //read out the pixels
	// for (int y = 0; y < m_size && !m_stop; y++)
	// {
	// for (int x = 0; x < m_size && !m_stop; x++)
	// {
	// pixel = XGetPixel(m_xim, x, y);
	//
	// rgb[0] = (pixel >> 16) & 0xff;
	// rgb[1] = (pixel >> 8) & 0xff;
	// rgb[2] = (pixel >> 0) & 0xff;
	//
	// boblight_addpixelxy(m_boblight, x, y, rgb);
	// }
	// }
	//
	// //send pixeldata to boblight
	// if (!boblight_sendrgb(m_boblight, m_sync, NULL))
	// {
	// m_error = boblight_geterror(m_boblight);
	// return true; //recoverable error
	// }
	//
	// //when in debug mode, put the captured image on the debug window
	// if (m_debug)
	// {
	// int x = (m_debugwindowwidth - m_size) / 2;
	// int y = (m_debugwindowheight - m_size) / 2;
	// XPutImage(m_debugdpy, m_debugwindow, m_debuggc, m_xim, 0, 0, x,
	// y, m_size, m_size);
	// XSync(m_debugdpy, False);
	// }
	//
	// if (!Wait())
	// {
	// #ifdef HAVE_LIBGL
	// m_error = m_vblanksignal.GetError();
	// #endif
	// return false; //unrecoverable error
	// }
	//
	// UpdateDebugFps();
	// }

	// }

	@Override
	public int[] grabPixelAt(int xpos, int ypos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cleanup() {

	}
}
