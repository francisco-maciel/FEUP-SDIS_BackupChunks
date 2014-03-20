package server;

public class DataChunk extends Chunk {

	private static final long serialVersionUID = 1L;

	private byte[] data;

	private int size;
 	public DataChunk(String fileId, int chunkNo, byte[] data, int size) {
		super(fileId, chunkNo);
		this.data = data;
		this.size = size;
	}

 	public byte[] getData() {
 		return data;
 	}

	public int getSize() {
		
		return this.size;
	}
}

