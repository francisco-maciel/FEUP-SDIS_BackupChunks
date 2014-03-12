package server.messages;


public class Stored extends Message {

	public Stored(String fileId, int chunkNo) {
		super(fileId, chunkNo);
	}

	public Stored(String version, String fileId, int chunkNo) {
		super(version, fileId, chunkNo);
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
