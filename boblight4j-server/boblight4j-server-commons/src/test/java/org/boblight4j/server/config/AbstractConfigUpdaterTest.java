package org.boblight4j.server.config;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.boblight4j.exception.BoblightException;
import org.boblight4j.server.ClientsHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AbstractConfigUpdaterTest {

	private AbstractConfigUpdater testable;

	@Mock
	private AbstractConfig config;
	@Mock
	private File watchFile;
	@Mock
	private ClientsHandler<?> clientsHandler;

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);

		testable = new AbstractConfigUpdater(watchFile, clientsHandler, config,
				null, null) {
		};

	}

	@Test
	public void testUpdateConfig() throws FileNotFoundException, IOException,
			BoblightException {
		testable.updateConfig();

		verify(clientsHandler).blockConnect(true);

		verify(config).clearConfig();
		verify(config).loadConfigFromFile(any(File.class));

		verify(clientsHandler).blockConnect(false);
	}
}
