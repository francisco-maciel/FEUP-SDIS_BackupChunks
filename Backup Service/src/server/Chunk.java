package server;

import java.io.Serializable;

public class Chunk implements Serializable {

	private static final long serialVersionUID = -408454220317663733L;
	String fileId;
	int chunkNo;
	byte[] data;
	
	public Chunk(String fileId, int chunkNo) {
		this.fileId = fileId;
		this.chunkNo = chunkNo;
	}
	
	public String getChunkFileName() {
		
		return fileId + "_" + chunkNo;
	}
	
	@Override public String toString() {
		
		return "FiledID: " + fileId + "\n" + "ChunkNo: " + chunkNo + "\n";
		
		
	}

	public String getName() {

		return getChunkFileName();
	}
	

}
