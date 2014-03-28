package server.messages;

import server.Version;

public class MessageIsLost extends Message {

	public MessageIsLost(String fileId) {
		super(fileId, 0);
		this.type = MessageType.ISLOST;
	}

	public MessageIsLost(String version, String fileId) {
		super(version, fileId, 0);
		this.type = MessageType.ISLOST;

	}

	public MessageIsLost(String fileId, int chunkNo) {
		super(fileId, chunkNo);
		this.type = MessageType.ISLOST;

	}

	@Override
	public String toMessage() {
		StringBuilder message = new StringBuilder();
		message.append(type);
		message.append(" ");
		message.append(Version.get());
		message.append(" ");
		message.append(fileId);
		message.append("\r\n");
		// second line could go here
		message.append("\r\n");

		return message.toString();
	}

	@Override
	public void setBody(byte[] bytes) {

	}

}
