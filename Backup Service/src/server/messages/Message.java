package server.messages;

public abstract class Message {
	
	protected MessageType type;
	protected String version;
	protected String fileId;
	protected int chunkNo;
	
	
	public Message( String fileId, int chunkNo) {
		this(Version.get(), fileId, chunkNo);       
    }
	
	public Message( String version , String fileId, int chunkNo) {
        this.fileId = fileId;
        this.version = version;
        this.type = null;
        this.chunkNo = chunkNo;
    }
	
	
	
	public MessageType getType() {
		return type;
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getFileId() {
		return fileId;
	}
	public int getChunkNo() {
		return chunkNo;
	}
	
	public void setVersion() {
		 this.version = Version.get();
	}
	public void setVersion(String version) {
		 this.version = version;
	}
	
	public void setFileId(String fileId) {
		 this.fileId = fileId;
	}
	
	public void setChunkNo(int chunkNo) {
		  this.chunkNo = chunkNo;
	}
	
	public abstract String toMessage();
}

