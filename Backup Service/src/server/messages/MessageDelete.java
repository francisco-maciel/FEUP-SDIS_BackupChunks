package server.messages;

import server.Version;

public class MessageDelete extends Message {

	public MessageDelete(String fileId) {
		this(Version.get(), fileId);
	}

	public MessageDelete(String version, String fileId) {
		super(version, fileId, 0);
		this.type = MessageType.DELETE;
	}

	@Override
	public String toMessage() {
		StringBuilder message = new StringBuilder();
		message.append(type);
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
