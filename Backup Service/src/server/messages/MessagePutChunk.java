package server.messages;

import java.io.UnsupportedEncodingException;

import server.Version;

public class MessagePutChunk extends Message {

	private byte[] body;
	private int replicationDeg;

	public MessagePutChunk(String fileId, int chunkNo, byte[] body,
			int replicationDeg) {
		this(Version.get(), fileId, chunkNo, body, replicationDeg);
	}

	public MessagePutChunk(String version, String fileId, int chunkNo,
			byte[] body, int replicationDeg) {
		super(version, fileId, chunkNo);
		this.type = MessageType.PUTCHUNK;
		this.body = body;
		this.replicationDeg = replicationDeg;

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
		message.append(" ");
		message.append(replicationDeg);
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

	@Override
	public void setBody(byte[] bytes) {
		body = bytes.clone();

	}

	public byte[] getBody() {
		return body;
	}

	public int getDegree() {
		return replicationDeg;
	}

}
