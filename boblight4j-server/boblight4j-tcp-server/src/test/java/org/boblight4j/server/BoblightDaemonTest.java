package org.boblight4j.server;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ System.class, BoblightDaemon.class })
@PowerMockIgnore({ "javax.management.*" })
public class BoblightDaemonTest {

	@Before
	public void setUp() throws Exception {
		PowerMockito.mockStatic(System.class);
		Method exit = Whitebox.getMethod(System.class, "exit", int.class);
		PowerMockito.suppress(exit);

	}

	@Test
	public void testMain() {

		BoblightDaemon.main("".split("\\s"));
	}
}
