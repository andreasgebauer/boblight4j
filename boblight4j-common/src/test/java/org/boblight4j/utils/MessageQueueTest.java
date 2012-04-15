package org.boblight4j.utils;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MessageQueueTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	private MessageQueue testable;

	@Before
	public void setUp() throws Exception {
		testable = new MessageQueue();
	}

	@Test
	public void testAddData() {
		String packet = "Packet1";
		testable.addData(packet);

		Assert.assertEquals(packet.length(), testable.getRemainingDataSize());
	}

	@Test
	public void testAddDataMultiLine() {
		String packet1 = "Packet1";
		String packet2 = "LongerPacket2";
		testable.addData(packet1 + "\n" + packet2);

		Assert.assertEquals(packet2.length(), testable.getRemainingDataSize());
		
		testable.nextMessage();
	}

	@Test
	public void testGetMessage() {
		testable.nextMessage();
	}

	@Test
	public void testGetNrMessages() {
		testable.getNrMessages();
	}

	@Test
	public void testGetRemainingDataSize() {
		testable.getRemainingDataSize();
	}

}
