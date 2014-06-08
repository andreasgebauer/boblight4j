package org.boblight4j.client.v4l;

import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.boblight4j.client.FlagManager;
import org.boblight4j.client.LightsHolder;
import org.boblight4j.client.SocketClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SocketClient.class, BoblightV4l.class })
public class BoblightV4lTest {

	@Before
	public void setup() {
		PowerMockito.mockStatic(System.class);
		PowerMockito.suppress(Whitebox.getMethod(System.class, "exit",
				int.class));
		PowerMockito.mockStatic(Thread.class);
		PowerMockito.suppress(Whitebox.getMethod(Thread.class, "sleep",
				long.class));
	}

	@Test
	public void testExitWhenSetupFails() throws Exception {

		final BoblightV4l testable = new BoblightV4l("".split("\\s"));

		final SocketClient client = Mockito.mock(SocketClient.class);
		whenNew(SocketClient.class).withArguments(any(LightsHolder.class))
				.thenReturn(client);

		when(client.setup(Matchers.isA(FlagManager.class))).thenAnswer(
				new Answer<Boolean>() {

					@Override
					public Boolean answer(InvocationOnMock invocation)
							throws Throwable {
						Whitebox.setInternalState(testable, "stop", true);
						return false;
					}
				});

		whenNew(BoblightV4l.class).withArguments(any(String[].class))
				.thenReturn(testable);

		BoblightV4l.main("".split("\\s"));

		PowerMockito.verifyStatic();
		System.exit(1);
	}
}
