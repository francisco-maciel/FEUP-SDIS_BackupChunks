package server;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Vector;

public class Chunk implements Serializable {

	private static final long serialVersionUID = 1L;
	String fileId;
	int chunkNo;
	public int desiredDegree;
	public int actualDegree;
	public Vector<String> origins;
	protected int size;

	public Chunk(String fileId, int chunkNo, int size) {
		this.fileId = fileId;
		this.chunkNo = chunkNo;
		desiredDegree = 0;
		actualDegree = 0;
		origins = new Vector<String>();
		this.size = size;

	}

	public String getChunkFileName() {

		return fileId + "_" + chunkNo;
	}

	@Override
	public String toString() {

		return "FiledID: " + fileId + "\n" + "ChunkNo: " + chunkNo + "\n"
				+ "Desired Degree: " + desiredDegree + "\n" + "Actual Degree: "
				+ actualDegree + "\n" + "Size: " + size + "\n";

	}

	public String getName() {

		return fileId;
	}

	public int getNo() {

		return chunkNo;
	}

	public Vector<String> getOrigins() {
		return origins;
	}

	public void addOrigin(InetAddress origin) {
		origins.add(origin.getCanonicalHostName());
	}

	public int getSize() {

		return this.size;
	}

}
