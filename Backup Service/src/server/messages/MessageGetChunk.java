package server.messages;

import server.Version;

public class MessageGetChunk extends Message {

	int port;

	public MessageGetChunk(String fileId, int chunkNo) {
		this(Version.get(), fileId, chunkNo);
		port = 0;
	}

	public MessageGetChunk(String version, String fileId, int chunkNo) {
		super(version, fileId, chunkNo);
		this.type = MessageType.GETCHUNK;
		port = 0;

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
		message.append("EXTENDED 8700");
		message.append("\r\n");
		message.append("\r\n");

		return message.toString();
	}

	@Override
	public void setBody(byte[] bytes) {

	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}

}
