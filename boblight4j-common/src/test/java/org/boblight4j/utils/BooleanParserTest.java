package org.boblight4j.utils;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.boblight4j.exception.BoblightParseException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class BooleanParserTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testParse() throws BoblightParseException {
		assertTrue(BooleanParser.parse(" true"));
		assertTrue(BooleanParser.parse("on"));
		assertTrue(BooleanParser.parse("yes"));
		assertTrue(BooleanParser.parse("1"));
		assertTrue(BooleanParser.parse("10"));
		assertTrue(BooleanParser.parse("100"));

		assertFalse(BooleanParser.parse("false"));
		assertFalse(BooleanParser.parse("off"));
		assertFalse(BooleanParser.parse("no"));
		assertFalse(BooleanParser.parse("0"));
	}

	@Test
	public void testParseFail() throws BoblightParseException {

		thrown.expectMessage("Unable to parse boolean value hastenichtgesehen");

		BooleanParser.parse("hastenichtgesehen");
	}

}
