package server.messages;

public class PutChunk extends Message {

	private byte[] body;
	private int replicationDeg;
	
	
	public PutChunk(String fileId, int chunkNo, byte[] body, int replicationDeg) {
		this(Version.get(),fileId, chunkNo, body, replicationDeg);		
	}

	public PutChunk(String version, String fileId, int chunkNo, byte[] body,int replicationDeg) {
		super(version, fileId, chunkNo);
		this.type = MessageType.PUTCHUNK;
		this.body = body;
		this.replicationDeg = replicationDeg;
	}

	@Override
	public String toMessage() {
		StringBuilder message = new StringBuilder();
		message.append(type); message.append(" ");
		message.append(fileId); message.append(" ");
		message.append(chunkNo); message.append(" ");
		message.append(replicationDeg);
		message.append("\r\n");
		// second line could go here
		message.append("\r\n");
		message.append(new String(body));
		
		return message.toString();
	}

}
