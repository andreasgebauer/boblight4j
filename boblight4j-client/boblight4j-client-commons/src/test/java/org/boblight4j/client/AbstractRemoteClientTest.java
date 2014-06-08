package org.boblight4j.client;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.boblight4j.exception.BoblightException;
import org.boblight4j.utils.Message;
import org.boblight4j.utils.Pointer;
import org.junit.Before;
import org.junit.Test;

public class AbstractRemoteClientTest {

    private AbstractRemoteClient testable;

    @Before
    public void setUp() throws Exception {
	testable = spy(new AbstractRemoteClient(mock(LightsHolder.class)) {

	    @Override
	    public void sendRgb(boolean sync, Pointer<Integer> outputused) throws BoblightException {

	    }

	    @Override
	    public void sendPriority(int priority) throws BoblightException {

	    }

	    @Override
	    public void sendOption(Light light, String option)
		    throws BoblightException {

	    }

	    @Override
	    public Message nextMessage() throws BoblightException {
		return null;
	    }

	    @Override
	    public LightsHolder getLightsHolder() {
		return null;
	    }

	    @Override
	    public void destroy() {

	    }

	    @Override
	    public void connect(String address, int port, int i)
		    throws BoblightException {
	    }

	    @Override
	    public boolean setup(FlagManager flagManager) {
		// TODO Auto-generated method stub
		return false;
	    }
	});
    }

    @Test
    public void test() throws BoblightException {
	final Message message = new Message();
	message.message.append("lights 1");

	final Message msg2 = new Message();
	msg2.message.append("light red scan 33 44 55 66");
	when(this.testable.nextMessage()).thenReturn(msg2);

	testable.parseLights(message);
    }

}
