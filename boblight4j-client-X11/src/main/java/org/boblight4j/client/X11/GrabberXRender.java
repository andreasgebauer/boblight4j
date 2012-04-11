package org.boblight4j.client.X11;

import gnu.x11.Pixmap;
import gnu.x11.extension.NotFoundException;
import gnu.x11.extension.render.DrawablePicture;
import gnu.x11.extension.render.DrawablePicture.Attributes;
import gnu.x11.extension.render.PictFormat;
import gnu.x11.extension.render.PictFormat.Type;
import gnu.x11.extension.render.Picture;
import gnu.x11.extension.render.Render;

import org.boblight4j.client.ClientImpl;
import org.boblight4j.exception.BoblightConfigurationException;

import com.sun.jna.examples.unix.X11;
import com.sun.jna.examples.unix.X11.Display;

/**
 * clients/boblight-X11/grabber-xrender.cpp
 * 
 * Does not work right now.
 * 
 * @author agebauer
 * 
 */
public class GrabberXRender extends AbstractX11Grabber {

	private Picture srcPicture;
	private Picture dstPicture;
	private Pixmap pixmap;
	private Render render;
	private Picture mask;

	public GrabberXRender(final ClientImpl boblight, final boolean sync,
			final int width, final int height, double interval) {
		super(boblight, sync, width, height, interval);
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
	protected void extendedSetup() throws BoblightConfigurationException {

		final X11 x11 = X11.INSTANCE;
		Display display = x11.XOpenDisplay(null);

		try
		{
			render = new Render(this.getDisplay());
		}
		catch (NotFoundException e)
		{
			throw new BoblightConfigurationException("", e);
		}

		final String[] extensions = this.getDisplay().extensions();

		pixmap = new Pixmap(this.getDisplay(), width, height);

		this.getDisplay().check_error();

		final PictFormat.Template template = new PictFormat.Template();

		// PictFormat[type=DIRECT, depth=32, direct=Direct[red_shift=0,
		// red_mask=255, green_shift=8, green_mask=255, blue_shift=16,
		// blue_mask=255, alpha_shift=0, alpha_mask=0]]
		template.set_type(Type.DIRECT);
		template.set_depth(32);
		template.set_direct(0, 255, 8, 255, 16, 255, 0, 0);

		final PictFormat format = render.picture_format(template, true);

		final Attributes attrs = DrawablePicture.Attributes.EMPTY;
		this.srcPicture = render.create_picture(this.getDisplay().default_root,
				format, attrs);

		this.getDisplay().check_error();

		this.dstPicture = render.create_picture(this.pixmap, format, attrs);

		this.getDisplay().check_error();

		this.mask = render.create_picture(new Pixmap(this.getDisplay(), width,
				height), format, attrs);

		this.getDisplay().check_error();

	}

	@Override
	public int[] grabPixelAt(int xpos, int ypos) {

		this.updateDimensions();

		render.composite(Render.SRC, this.srcPicture, this.mask,
				this.dstPicture, 0, 0, 0, 0, 0, 0, this.width, this.height);

		this.getDisplay().check_error();

		return null;
	}

	@Override
	public void cleanup() {
		this.srcPicture.unintern();
		this.dstPicture.unintern();
	}
}
