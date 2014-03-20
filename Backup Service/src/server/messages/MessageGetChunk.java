package server.messages;

import server.Version;

public class MessageGetChunk extends Message {

	public MessageGetChunk(String fileId, int chunkNo) {
		this(Version.get(), fileId, chunkNo);
	}

	public MessageGetChunk(String version, String fileId, int chunkNo) {
		super(version, fileId, chunkNo);
		this.type = MessageType.GETCHUNK;
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

		return message.toString();
	}

	@Override
	public void setBody(byte[] bytes) {

	}

}
