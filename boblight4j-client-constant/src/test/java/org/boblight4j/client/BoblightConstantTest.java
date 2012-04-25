package org.boblight4j.client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ BoblightConstant.class })
public class BoblightConstantTest {

	@Before
	public void setUp() throws Exception {
		PowerMockito.mockStatic(System.class);
		PowerMockito.suppress(Whitebox.getMethod(System.class, "exit",
				int.class));
	}

	@Test
	public void test() {
		new BoblightConstant(new String[] { "-h" });
	}

}
