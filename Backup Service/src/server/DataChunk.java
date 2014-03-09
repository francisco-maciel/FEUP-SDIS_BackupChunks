package server;

public class DataChunk extends Chunk {

	private static final long serialVersionUID = 1L;

	private byte[] data;
 	public DataChunk(String fileId, int chunkNo, byte[] data) {
		super(fileId, chunkNo);
		this.data = data;
	}

 	public byte[] getData() {
 		return data;
 	}
}

