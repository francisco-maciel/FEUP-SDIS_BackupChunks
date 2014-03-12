package server.messages;

import server.Version;
import server.messages.Message;

public class MessageRemoved extends Message {

	public MessageRemoved(String fileId, int chunkNo) {
		this(Version.get(), fileId, chunkNo);
	}

	public MessageRemoved(String version, String fileId, int chunkNo) {
		super(version, fileId, chunkNo);
		this.type = MessageType.REMOVED;
	}

	@Override
	public String toMessage() {
		
		StringBuilder message = new StringBuilder();
		message.append(type); message.append(" ");
		message.append(fileId); message.append(" ");
		message.append(chunkNo);
		message.append("\r\n");
		// second line could go here
		message.append("\r\n");
		
		return message.toString();
	}

}
