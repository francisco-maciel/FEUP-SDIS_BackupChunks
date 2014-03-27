package server.messages;

import java.io.UnsupportedEncodingException;

import server.Version;

public class MessageChunk extends Message {

	private byte[] body;

	public MessageChunk(String fileId, int chunkNo, byte[] body) {
		this(Version.get(), fileId, chunkNo, body);
	}

	public MessageChunk(String version, String fileId, int chunkNo, byte[] body) {
		super(version, fileId, chunkNo);
		this.type = MessageType.CHUNK;
		this.body = body;
	}

	@Override
	public String toMessage() {
		StringBuilder message = new StringBuilder();
		message.append(type);
		message.append(" ");
		message.append(version);
		message.append(" ");
		message.append(fileId);
		message.append(" ");
		message.append(chunkNo);
		message.append("\r\n");
		// second line could go here
		message.append("\r\n");
		try {
			message.append(new String(body, "ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return message.toString();
	}

	public byte[] getBody() {
		return body;
	}

	@Override
	public void setBody(byte[] bytes) {
		body = bytes.clone();

	}

}
