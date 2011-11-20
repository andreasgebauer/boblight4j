/*
 * Created on Jul 25, 2008
 *
 * Spectro-Edit is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spectro-Edit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package org.boblight4j.client;

import java.util.Arrays;

import org.apache.log4j.Logger;

public class VorbisWindowFunction implements WindowFunction {

	private static final Logger LOG = Logger
			.getLogger(VorbisWindowFunction.class);

	private static final double PI = Math.PI;

	private final double[] scalars;

	public VorbisWindowFunction(final int size) {
		this.scalars = new double[size];
		for (int i = 0; i < size; i++)
		{

			// This is the real vorbis one, but it's designed for MDCT where
			// the output array is half the size of the input array
			// double xx = Math.sin( (PI/(2.0*size)) * (i + 0.5) );

			final double xx = Math.sin(PI / (2.0 * size) * (2.0 * i));
			this.scalars[i] = Math.sin(PI / 2.0 * (xx * xx));
		}
		LOG.debug(String.format("VorbisWindowFunction scalars (size=%d): %s\n",
				this.scalars.length, Arrays.toString(this.scalars)));
	}

	@Override
	public void applyWindow(final double[] data) {
		if (data.length != this.scalars.length)
		{
			throw new IllegalArgumentException("Invalid array size (required: "
					+ this.scalars.length + "; given: " + data.length + ")");
		}
		for (int i = 0; i < data.length; i++)
		{
			data[i] *= this.scalars[i];
		}
	}

}
