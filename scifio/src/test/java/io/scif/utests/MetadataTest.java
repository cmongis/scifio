/*
 * #%L
 * SCIFIO library for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2011 - 2014 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package io.scif.utests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.scif.FormatException;
import io.scif.Metadata;
import io.scif.SCIFIO;
import io.scif.img.axes.SCIFIOAxes;
import io.scif.util.FormatTools;

import java.io.IOException;

import net.imglib2.meta.Axes;

import org.junit.Test;

/**
 * Unit tests for {@link io.scif.Metadata} interface methods.
 * 
 * @author Mark Hiner
 */
public class MetadataTest {
	
	private final SCIFIO scifio = new SCIFIO();
	private final String id =
			"testImg&lengths=620,512,5&axes=X,Y,Time,Z,Channel.fake";
	private final String ndId =
			"ndImg&axes=X,Y,Z,Channel,Time,Lifetime,Spectra,&lengths=256,128,2,6,10,4,8.fake";

	/**
	 * Down the middle test that verifies each method of the Metadata API.
	 * @throws FormatException 
	 * @throws IOException 
	 */
	@Test
	public void testDownTheMiddle() throws IOException, FormatException {
		Metadata m = scifio.format().getFormat(id).createParser().parse(id);
		
		// Check getAxisType(int, int)
		assertEquals(m.get(0).getAxis(0).type(), Axes.X);
		assertEquals(m.get(0).getAxis(1).type(), Axes.Y);
		assertEquals(m.get(0).getAxis(2).type(), Axes.TIME);
		assertEquals(m.get(0).getAxis(3).type(), Axes.Z);
		assertEquals(m.get(0).getAxis(4).type(), Axes.CHANNEL);
		
		// Check getAxisLength(int, int)
		assertEquals(m.get(0).getAxisLength(0), 620);
		assertEquals(m.get(0).getAxisLength(1), 512);
		assertEquals(m.get(0).getAxisLength(2), 5);
		assertEquals(m.get(0).getAxisLength(3), 1);
		assertEquals(m.get(0).getAxisLength(4), 1);
		
		// Check getAxisLength(int, AxisType)
		assertEquals(m.get(0).getAxisLength(Axes.X), 620);
		assertEquals(m.get(0).getAxisLength(Axes.Y), 512);
		assertEquals(m.get(0).getAxisLength(Axes.TIME), 5);
		assertEquals(m.get(0).getAxisLength(Axes.Z), 1);
		assertEquals(m.get(0).getAxisLength(Axes.CHANNEL), 1);
		
		// Check getAxisIndex(int, AxisType)
		assertEquals(m.get(0).getAxisIndex(Axes.X), 0);
		assertEquals(m.get(0).getAxisIndex(Axes.Y), 1);
		assertEquals(m.get(0).getAxisIndex(Axes.TIME), 2);
		assertEquals(m.get(0).getAxisIndex(Axes.Z), 3);
		assertEquals(m.get(0).getAxisIndex(Axes.CHANNEL), 4);
	}
	
	/**
	 * Verify conditions when adding axes
	 * 
	 * @throws FormatException
	 */
	@Test
	public void testAddingAxes() throws FormatException {
		Metadata m = scifio.format().getFormat(id).createMetadata();
		m.createImageMetadata(1);
		
		// Verify that, after adding an axis to a clean metadata, the axis
		// length and type can be looked up properly
		assertEquals(m.get(0).getAxisLength(Axes.X), 1);
		assertEquals(m.get(0).getAxisIndex(Axes.X), -1);
		m.get(0).setAxisLength(Axes.X, 100);
		assertEquals(m.get(0).getAxisLength(Axes.X), 100);
		assertEquals(m.get(0).getAxisIndex(Axes.X), 0);
	}
	
	/**
	 * Verify conditions when interrogating non-existant axes
	 * 
	 * @throws FormatException 
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testMissingAxes() throws FormatException {
		Metadata m = scifio.format().getFormat(id).createMetadata();
		
		// Axis index should be -1, length 0
		assertEquals(m.get(0).getAxisLength(Axes.X), -1);
		assertEquals(m.get(0).getAxisLength(Axes.X), 0);
		
		// Should throw an IndexOutOfBoundsException
		assertEquals(m.get(0).getAxisLength(0), 0);
	}
	
	/**
	 * Down the middle testing of constructing an N-D image.
	 */
	@Test
	public void testNDBasic() throws FormatException, IOException {
		Metadata m = scifio.initializer().parseMetadata(ndId);

		// Basic plane + axis length checks
		assertEquals(2 * 6 * 10 * 4 * 8, m.get(0).getPlaneCount());
		assertEquals(8, m.get(0).getAxisLength(SCIFIOAxes.SPECTRA));
		assertEquals(4, m.get(0).getAxisLength(SCIFIOAxes.LIFETIME));
		assertEquals(10, m.get(0).getAxisLength(Axes.TIME));
		assertEquals(6, m.get(0).getAxisLength(Axes.CHANNEL));
		assertEquals(2, m.get(0).getAxisLength(Axes.Z));
	}

	/**
	 * Check Plane Index lookups via
	 * {@link FormatTools#positionToRaster(long[], long[])} with an N-D dataset.
	 */
	@Test
	public void testNDPositions() throws FormatException, IOException {
		Metadata m = scifio.initializer().parseMetadata(ndId);

		// Plane index lookup checks
		long[] pos = { 1, 3, 5, 0, 0 };
		assertEquals(1 + (3 * 2) + (5 * 6 * 2), FormatTools.positionToRaster(m
			.get(0).getAxesLengthsNonPlanar(), pos));

		pos = new long[] { 0, 0, 3, 3, 7 };
		assertEquals((3 * 6 * 2) + (3 * 10 * 6 * 2) + (7 * 4 * 10 * 6 * 2),
			FormatTools.positionToRaster(m.get(0).getAxesLengthsNonPlanar(), pos));
	}

	/**
	 * Test that the plane count reflects updates to the planar axis count in
	 * an N-D dataset.
	 */
	@Test
	public void testNDPlaneCounts() throws FormatException, IOException {
		Metadata m = scifio.initializer().parseMetadata(ndId);

		// Try adjusting the planar axis count.
		m.get(0).setPlanarAxisCount(3);
		assertEquals(6 * 10 * 4 * 8, m.get(0).getPlaneCount());
		m.get(0).setPlanarAxisCount(4);
		assertEquals(10 * 4 * 8, m.get(0).getPlaneCount());
	}

	/**
	 * Test that axis-position-dependent flags (e.g. multichannel, interleaved)
	 * reflect updates to axis positions with an N-D dataset.
	 */
	@Test
	public void testNDFlags() throws FormatException, IOException {
		Metadata m = scifio.initializer().parseMetadata(ndId);
		// Check multichannel. C index < planar axis count, so should be false
		assertFalse(m.get(0).isMultichannel());
		// Check the interleaved flag
		// XY...C.. so not interleaved
		assertFalse(m.get(0).getInterleavedAxisCount() > 0);
		m.get(0).setPlanarAxisCount(4);
		// Now multichannel
		assertTrue(m.get(0).isMultichannel());
		// But still XY...C
		assertFalse(m.get(0).getInterleavedAxisCount() > 0);
		m.get(0).setAxisType(0, Axes.CHANNEL);
		m.get(0).setInterleavedAxisCount(1);
		// Now we're CXY, so interleaved
		assertEquals(1, m.get(0).getAxisIndex(Axes.X));
		assertEquals(2, m.get(0).getAxisIndex(Axes.Y));
		assertTrue(m.get(0).getInterleavedAxisCount() > 0);
	}
}
