package org.boblight4j.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MiscTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetWordPointerOfStringPointerOfString() throws Exception {
		final Pointer<String> line = new Pointer<String>("word key value");
		final String parsedWord = Misc.getWord(line);

		Assert.assertEquals("word", parsedWord);
	}

	@Test
	public void testGetWordStringBuilderPointerOfString() throws Exception {
		String parsedWord;
		final StringBuilder line = new StringBuilder(
				"set light bottom1 rgb 1.0 1.0 1.0\n");
		parsedWord = Misc.getWord(line);
		Assert.assertEquals("set", parsedWord);
		parsedWord = Misc.getWord(line);
		Assert.assertEquals("light", parsedWord);
		parsedWord = Misc.getWord(line);
		Assert.assertEquals("bottom1", parsedWord);
	}

}
