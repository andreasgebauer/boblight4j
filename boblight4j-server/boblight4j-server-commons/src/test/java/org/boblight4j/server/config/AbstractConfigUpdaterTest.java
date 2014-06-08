package org.boblight4j.server.config;

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
    @Mock
    private ConfigCreator configCreator;

    @Before
    public void setUp() throws Exception {

	MockitoAnnotations.initMocks(this);

	this.testable = new AbstractConfigUpdater(this.watchFile, this.configCreator, this.clientsHandler, this.config) {
	};

    }

    @Test
    public void testUpdateConfig() throws FileNotFoundException, IOException, BoblightException {
	this.testable.updateConfig();

	verify(this.clientsHandler).blockConnect(true);
	verify(this.config).clear();
	verify(this.clientsHandler).blockConnect(false);
    }
}
