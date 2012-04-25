package org.boblight4j.server.config;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.boblight4j.device.DeviceRS232;
import org.boblight4j.exception.BoblightException;
import org.boblight4j.server.SocketClientsHandlerImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;

public class TcpServerConfigImplTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Rule
	public ExpectedException ex = ExpectedException.none();

	@Mock
	private SocketClientsHandlerImpl clientsHandler;

	private TcpServerConfigImpl testable;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.testable = new TcpServerConfigImpl(null);
	}

	@Test
	public void testClearConfig() {
		this.testable.clearConfig();
	}

}
