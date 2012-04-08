package org.boblight4j.client.v4l;

import org.boblight4j.client.ClientImpl;
import org.boblight4j.exception.BoblightException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ClientImpl.class, BoblightV4l.class })
public class BoblightV4lTest {

	@Before
	public void setup() {
	}

	@Test
	@Ignore
	public void testMain() throws Exception {

		// Boblight mock = Mockito.mock(Boblight.class);
		// PowerMockito.whenNew(Boblight.class).withNoArguments().thenReturn(mock);
		final ClientImpl mockClient = Mockito.mock(ClientImpl.class);
		PowerMockito.whenNew(ClientImpl.class).withNoArguments()
				.thenReturn(mockClient);

		PowerMockito
				.doThrow(new BoblightException(""))
				.when(mockClient)
				.connect(Matchers.anyString(), Matchers.anyInt(),
						Matchers.anyInt());

		BoblightV4l.main("".split("\\s"));

	}

}
