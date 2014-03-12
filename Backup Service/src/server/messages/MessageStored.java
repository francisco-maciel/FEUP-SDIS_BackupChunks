package server.messages;

import server.Version;


public class MessageStored extends Message {

	public MessageStored(String fileId, int chunkNo) {
		this(Version.get(), fileId, chunkNo);
	}

	public MessageStored(String version, String fileId, int chunkNo) {
		super(version, fileId, chunkNo);
		this.type =  MessageType.STORED;
	}

	@Override
	public String toMessage() {
		StringBuilder message = new StringBuilder();
		message.append(type); message.append(" ");
		message.append(version); message.append(" ");
		message.append(fileId); message.append(" ");
		message.append(chunkNo);
		message.append("\r\n");
		// second line could go here
		message.append("\r\n");
		
		return message.toString();
	}

}
