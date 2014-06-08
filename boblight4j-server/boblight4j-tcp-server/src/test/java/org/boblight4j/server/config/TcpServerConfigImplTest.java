package org.boblight4j.server.config;

import org.boblight4j.server.SocketClientsHandlerImpl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
		this.testable.clear();
	}

}
