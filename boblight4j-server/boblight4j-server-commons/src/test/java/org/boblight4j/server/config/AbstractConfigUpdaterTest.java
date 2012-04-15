package org.boblight4j.server.config;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.boblight4j.exception.BoblightException;
import org.boblight4j.server.ClientsHandler;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AbstractConfigUpdaterTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	private AbstractConfigUpdater testable;

	private AbstractConfig config;
	private File watchFile;
	private ClientsHandler clientsHandler;

	@Before
	public void setUp() throws Exception {
		config = mock(AbstractConfig.class);
		watchFile = mock(File.class);
		clientsHandler = mock(ClientsHandler.class);
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
