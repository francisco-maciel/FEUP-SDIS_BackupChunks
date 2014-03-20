package server.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import server.Version;
import server.messages.Message;
import server.messages.MessageChunk;
import server.messages.MessageDelete;
import server.messages.MessageGetChunk;
import server.messages.MessagePutChunk;
import server.messages.MessageRemoved;
import server.messages.MessageStored;
import server.messages.UnrecognizedMessageException;

public class MessageTests {

	@Test
	public void testMessageChunk() {
		String message = "CHUNK "+Version.get()+" chunkName 0\r\n\r\nbody";
		
		// test message.tostring
		Message m = new MessageChunk("chunkName", 0, new String("body").getBytes());
		assertEquals(message,m.toMessage());	
		
		// test parse message
		try {
			Message parsed = Message.parse(message);
			assertNotNull(parsed);
			assertTrue(parsed instanceof MessageChunk);
			MessageChunk mc = (MessageChunk) parsed;
			assertEquals(mc.getChunkNo(),0);
			assertEquals(mc.getFileId(),"chunkName");
			assertEquals(new String(mc.getBody()),"body");
			assertEquals(mc.getVersion(),Version.get());
			
		} catch (UnrecognizedMessageException e) {
			fail("Unrecognized message received");
		}
		
	}
	
	
	@Test
	public void testMessageDelete() {
		String message = "DELETE chunkName\r\n\r\n";
		Message m = new MessageDelete("chunkName");
		assertEquals(message,m.toMessage());
		
		// test parse message
		try {
			Message parsed = Message.parse(message);
			assertNotNull(parsed);
			assertTrue(parsed instanceof MessageDelete);
			MessageDelete mc = (MessageDelete) parsed;
			assertEquals(mc.getFileId(),"chunkName");
			assertEquals(mc.getVersion(),Version.get());
			
		} catch (UnrecognizedMessageException e) {
			fail("Unrecognized message received");
		}
		
	}
	
	@Test
	public void testMessageGetChunk() {
		String message ="GETCHUNK "+Version.get()+" chunkName 0\r\n\r\n";
		Message m = new MessageGetChunk("chunkName", 0);
		assertEquals(message,m.toMessage());
		
		// test parse message
			try {
				Message parsed = Message.parse(message);
				assertNotNull(parsed);
				assertTrue(parsed instanceof MessageGetChunk);
				MessageGetChunk mc = (MessageGetChunk) parsed;
				assertEquals(mc.getChunkNo(),0);
				assertEquals(mc.getFileId(),"chunkName");
				assertEquals(mc.getVersion(),Version.get());
				
			} catch (UnrecognizedMessageException e) {
				fail("Unrecognized message received");
			}

	}
	
	@Test
	public void testMessagePutChunk() {
		String message = "PUTCHUNK "+Version.get()+" chunkName 0 10\r\n\r\nbody";
		Message m = new MessagePutChunk("chunkName", 0, new String("body").getBytes(), 10);
		assertEquals(message,m.toMessage());
		
		try {
			Message parsed = Message.parse(message);
			assertNotNull(parsed);
			assertTrue(parsed instanceof MessagePutChunk);
			MessagePutChunk mc = (MessagePutChunk) parsed;
			assertEquals(mc.getChunkNo(),0);
			assertEquals(mc.getFileId(),"chunkName");
			assertEquals(mc.getVersion(),Version.get());
			assertEquals(new String(mc.getBody()),"body");
			
		} catch (UnrecognizedMessageException e) {
			fail("Unrecognized message received");
		}
		
	}
	
	@Test
	public void testMessageRemoved() {
		String message = "REMOVED chunkName 0\r\n\r\n";
		Message m = new MessageRemoved("chunkName", 0);
		assertEquals(message,m.toMessage());
		
		try {
			Message parsed = Message.parse(message);
			assertNotNull(parsed);
			assertTrue(parsed instanceof MessageRemoved);
			MessageRemoved mc = (MessageRemoved) parsed;
			assertEquals(mc.getFileId(),"chunkName");
			assertEquals(mc.getVersion(),Version.get());
			
		} catch (UnrecognizedMessageException e) {
			fail("Unrecognized message received");
		}
			
	}
	
	@Test
	public void testMessageStored() {
		String message = "STORED "+Version.get()+" chunkName 0\r\n\r\n";
		Message m = new MessageStored("chunkName", 0);
		assertEquals(message,m.toMessage());	
		
		try {
			Message parsed = Message.parse(message);
			assertNotNull(parsed);
			assertTrue(parsed instanceof MessageStored);
			MessageStored mc = (MessageStored) parsed;
			assertEquals(mc.getChunkNo(),0);
			assertEquals(mc.getFileId(),"chunkName");
			assertEquals(mc.getVersion(),Version.get());
			
		} catch (UnrecognizedMessageException e) {
			fail("Unrecognized message received");
		}
		
	}
	
	@Test(expected=UnrecognizedMessageException.class)
	public void testUnrecognizedMessage() throws UnrecognizedMessageException {
		String message = "STORED "+Version.get()+" chunkName 0 extrasuff\r\ni dont enven\r\n know";
		
	
			Message parsed = Message.parse(message);
			fail("Accepted unknown message");		
		
	}
	
	@Test(expected=UnrecognizedMessageException.class)
	public void testUnrecognizedMessage2() throws UnrecognizedMessageException {
		String message = "NEWMESSAGE "+Version.get()+" chunkName 0 extrasuff";
		
			Message parsed = Message.parse(message);
			fail("Accepted unknown message");		
		
	}
	
	@Test(expected=UnrecognizedMessageException.class)
	public void testUnrecognizedMessage3() throws UnrecognizedMessageException {
		String message = null;
		
			Message parsed = Message.parse(message);
			fail("Accepted unknown message");			
	}
	
	@Test
	public void IgnoreHeaderLines()  {
		String message = "STORED "+Version.get()+" chunkName 0\r\nAlso did extra stuff\r\n\r\n";
	
		try {
			Message parsed = Message.parse(message);
			assertNotNull(parsed);
			assertTrue(parsed instanceof MessageStored);
			MessageStored mc = (MessageStored) parsed;
			assertEquals(mc.getChunkNo(),0);
			assertEquals(mc.getFileId(),"chunkName");
			assertEquals(mc.getVersion(),Version.get());
			
		} catch (UnrecognizedMessageException e) {
			fail("Unrecognized message received");
		}	
		
	}
	
	


}
