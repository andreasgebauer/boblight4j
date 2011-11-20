package org.boblight4j.utils;

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
		testable.addData("Packet");

	}

	@Test
	public void testGetMessage() {
		testable.getMessage();
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
