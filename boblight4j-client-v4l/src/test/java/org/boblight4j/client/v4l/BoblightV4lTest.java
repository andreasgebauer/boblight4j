package org.boblight4j.client.v4l;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;

import java.lang.reflect.Method;

import org.boblight4j.client.ClientImpl;
import org.boblight4j.exception.BoblightException;
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
@PrepareForTest({ ClientImpl.class, BoblightV4l.class})
public class BoblightV4lTest {

	@Before
	public void setup() {
		PowerMockito.mockStatic(System.class);
		Method exit = Whitebox.getMethod(System.class, "exit", int.class);
		PowerMockito.suppress(exit);
		PowerMockito.mockStatic(Thread.class);
		PowerMockito.suppress(Whitebox.getMethod(Thread.class, "sleep",
				long.class));
	}

	@Test
	public void testExitWhenSetupFails() throws Exception {

		final BoblightV4l testable = new BoblightV4l("".split("\\s"));

		final ClientImpl mockClient = Mockito.mock(ClientImpl.class);
		PowerMockito.whenNew(ClientImpl.class).withNoArguments()
				.thenReturn(mockClient);

		PowerMockito
				.doThrow(new BoblightException(""))
				.when(mockClient)
				.connect(Matchers.anyString(), Matchers.anyInt(),
						Matchers.anyInt());
		doAnswer(new Answer<Object>() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Whitebox.setInternalState(testable, "stop", true);
				return null;
			}
		}).when(mockClient).destroy();

		PowerMockito.whenNew(BoblightV4l.class)
				.withArguments(any(String[].class)).thenReturn(testable);

		BoblightV4l.main("".split("\\s"));

		PowerMockito.verifyStatic();
		System.exit(1);
	}
}
