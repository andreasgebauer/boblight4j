package org.boblight4j.server.config;

import static org.junit.Assert.*;

import org.boblight4j.exception.BoblightException;
import org.junit.Before;
import org.junit.Test;

public class AbstractConfigTest {

	private AbstractConfig testable;

	@Before
	public void setUp() throws Exception {
		testable = new AbstractConfig(null) {
		};
	}

}
