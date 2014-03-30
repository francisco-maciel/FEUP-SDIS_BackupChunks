package server.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.junit.Test;

import server.Version;
import server.messages.Message;
import server.messages.MessageChunk;
import server.messages.MessageDelete;
import server.messages.MessageGetChunk;
import server.messages.MessageIsLost;
import server.messages.MessagePutChunk;
import server.messages.MessageRemoved;
import server.messages.MessageStored;
import server.messages.UnrecognizedMessageException;

public class MessageTests {

	@Test
	public void testMessageChunk() {
		String message = "CHUNK " + Version.get() + " chunkName 0\r\n\r\n";

		// test message.tostring
		Message m = new MessageChunk("chunkName", 0, new String("").getBytes());
		assertEquals(message, m.toMessage());

		// test parse message
		try {
			Message parsed = Message.parse(message);
			assertNotNull(parsed);
			assertTrue(parsed instanceof MessageChunk);
			MessageChunk mc = (MessageChunk) parsed;
			assertEquals(mc.getChunkNo(), 0);
			assertEquals(mc.getFileId(), "chunkName");
			assertEquals(new String(mc.getBody()), "");
			assertEquals(mc.getVersion(), Version.get());

		} catch (UnrecognizedMessageException e) {
			fail("Unrecognized message received");
		}

	}

	@Test
	public void testMessageDelete() {
		String message = "DELETE chunkName\r\n\r\n";
		Message m = new MessageDelete("chunkName");
		assertEquals(message, m.toMessage());

		// test parse message
		try {
			Message parsed = Message.parse(message);
			assertNotNull(parsed);
			assertTrue(parsed instanceof MessageDelete);
			MessageDelete mc = (MessageDelete) parsed;
			assertEquals(mc.getFileId(), "chunkName");
			assertEquals(mc.getVersion(), Version.get());

		} catch (UnrecognizedMessageException e) {
			fail("Unrecognized message received");
		}

	}

	@Test
	public void testMessageGetChunk() {
		String message = "GETCHUNK " + Version.get()
				+ " chunkName 0\r\nEXTENDED 8700\r\n\r\n";
		Message m = new MessageGetChunk("chunkName", 0);
		assertEquals(message, m.toMessage());

		// test parse message
		try {
			Message parsed = Message.parse(message);
			assertNotNull(parsed);
			assertTrue(parsed instanceof MessageGetChunk);
			MessageGetChunk mc = (MessageGetChunk) parsed;
			assertEquals(mc.getChunkNo(), 0);
			assertEquals(mc.getPort(), 8700);
			assertEquals(mc.getFileId(), "chunkName");
			assertEquals(mc.getVersion(), Version.get());

		} catch (UnrecognizedMessageException e) {
			fail("Unrecognized message received");
		}

	}

	@Test
	public void testMessagePutChunk() {
		try {
			String message = new String(
					("PUTCHUNK " + Version.get() + " chunkName 0 10\r\n\r\nbody\n\nbodyš\n")
							.getBytes("ISO-8859-1"), "ISO-8859-1");

			Message m;

			m = new MessagePutChunk("chunkName", 0,
					"body\n\nbodyš\n".getBytes("ISO-8859-1"), 10);

			assertEquals(message, m.toMessage());
			try {
				Message parsed = Message.parse(m.toMessage());
				assertNotNull(parsed);
				assertTrue(parsed instanceof MessagePutChunk);
				MessagePutChunk mc = (MessagePutChunk) parsed;
				assertEquals(mc.getChunkNo(), 0);
				assertEquals(mc.getFileId(), "chunkName");
				assertEquals(mc.getVersion(), Version.get());
				assertTrue(Arrays.equals(
						"body\n\nbodyš\n".getBytes("ISO-8859-1"),
						((MessagePutChunk) parsed).getBody()));

			} catch (UnrecognizedMessageException e) {
				fail("Unrecognized message received");
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
	}

	@Test
	public void testMessageRemoved() {
		String message = "REMOVED chunkName 0\r\n\r\n";
		Message m = new MessageRemoved("chunkName", 0);
		assertEquals(message, m.toMessage());

		try {
			Message parsed = Message.parse(message);
			assertNotNull(parsed);
			assertTrue(parsed instanceof MessageRemoved);
			MessageRemoved mc = (MessageRemoved) parsed;
			assertEquals(mc.getFileId(), "chunkName");
			assertEquals(mc.getVersion(), Version.get());

		} catch (UnrecognizedMessageException e) {
			fail("Unrecognized message received");
		}

	}

	@Test
	public void testMessageStored() {
		String message = "STORED " + Version.get() + " chunkName 0\r\n\r\n";
		Message m = new MessageStored("chunkName", 0);
		assertEquals(message, m.toMessage());

		try {
			Message parsed = Message.parse(message);
			assertNotNull(parsed);
			assertTrue(parsed instanceof MessageStored);
			MessageStored mc = (MessageStored) parsed;
			assertEquals(mc.getChunkNo(), 0);
			assertEquals(mc.getFileId(), "chunkName");
			assertEquals(mc.getVersion(), Version.get());

		} catch (UnrecognizedMessageException e) {
			fail("Unrecognized message received");
		}

	}

	@Test
	public void testMessageIsLost() {
		String message = "ISLOST " + Version.get() + " chunkName\r\n\r\n";
		Message m = new MessageIsLost("chunkName");
		assertEquals(message, m.toMessage());

		try {
			Message parsed = Message.parse(message);
			assertNotNull(parsed);
			assertTrue(parsed instanceof MessageIsLost);
			MessageIsLost mc = (MessageIsLost) parsed;
			assertEquals(mc.getChunkNo(), 0);
			assertEquals(mc.getFileId(), "chunkName");
			assertEquals(mc.getVersion(), Version.get());

		} catch (UnrecognizedMessageException e) {
			fail("Unrecognized message received");
		}

	}

	@Test(expected = UnrecognizedMessageException.class)
	public void testUnrecognizedMessage() throws UnrecognizedMessageException {
		String message = "STORED " + Version.get()
				+ " chunkName 0 extrasuff\r\ni dont enven\r\n know";

		@SuppressWarnings("unused")
		Message parsed = Message.parse(message);
		fail("Accepted unknown message");

	}

	@Test(expected = UnrecognizedMessageException.class)
	public void testUnrecognizedMessage2() throws UnrecognizedMessageException {
		String message = "NEWMESSAGE " + Version.get()
				+ " chunkName 0 extrasuff";

		@SuppressWarnings("unused")
		Message parsed = Message.parse(message);
		fail("Accepted unknown message");

	}

	@Test(expected = UnrecognizedMessageException.class)
	public void testUnrecognizedMessage3() throws UnrecognizedMessageException {
		String message = null;

		@SuppressWarnings("unused")
		Message parsed = Message.parse(message);
		fail("Accepted unknown message");
	}

	@Test
	public void IgnoreHeaderLines() {
		String message = "STORED " + Version.get()
				+ " chunkName 0\r\nAlso did extra stuff\r\n\r\n";

		try {
			Message parsed = Message.parse(message);
			assertNotNull(parsed);
			assertTrue(parsed instanceof MessageStored);
			MessageStored mc = (MessageStored) parsed;
			assertEquals(mc.getChunkNo(), 0);
			assertEquals(mc.getFileId(), "chunkName");
			assertEquals(mc.getVersion(), Version.get());

		} catch (UnrecognizedMessageException e) {
			fail("Unrecognized message received");
		}

	}

}
