package org.boblight4j.client.X11;

import gnu.x11.Display;
import gnu.x11.Option;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class DisplayTest {

	@Before
	public void setUp() {

	}

	@Test
	@Ignore
	public void testGetDisplay() {

		final String env = gnu.util.Environment.value("DISPLAY");

		final Option option = new Option(null);
		final Display.Name display_name = option.display_name("display",
				"X server to connect to", new Display.Name(env));

		final Display display = new Display();

		final Display display2 = display.default_root.display;

	}
}
