package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;

public class Chunk implements Serializable {

	private static final long serialVersionUID = 1L;
	String fileId;
	int chunkNo;
	public int desiredDegree;
	public HashSet<String> origins;
	protected int size;

	public Chunk(String fileId, int chunkNo, int size) {
		this.fileId = fileId;
		this.chunkNo = chunkNo;
		desiredDegree = 0;
		this.size = size;
		origins = new HashSet<String>();

	}

	public String getChunkFileName() {

		return fileId + "_" + chunkNo;
	}

	@Override
	public String toString() {

		return "FiledID: " + fileId + "\n" + "ChunkNo: " + chunkNo + "\n"
				+ "Desired Degree: " + desiredDegree + "\n" + "Actual Degree: "
				+ origins.size() + "\n" + "Size: " + size + "\n";

	}

	public String getName() {

		return fileId;
	}

	public int getNo() {

		return chunkNo;
	}

	public int getSize() {

		return this.size;
	}

	@SuppressWarnings("unchecked")
	public void setOrigins(HashSet<String> origin) {
		this.origins = (HashSet<String>) origin.clone();
	}

	public HashSet<String> getOrigins() {
		return this.origins;
	}

	public synchronized boolean incrementDegree(String origin) {
		return origins.add(origin);
	}

	public synchronized int getActualDegree() {
		return origins.size();
	}

	public DataChunk getDataChunk() {
		DataChunk result = null;

		File f = new File("data" + File.separator + "chunks" + File.separator
				+ getChunkFileName());

		if (f.exists() && !f.isDirectory()) {
			try {
				InputStream file = new FileInputStream(f);

				byte[] data = new byte[64000];
				int read = file.read(data);
				byte[] body = Arrays.copyOf(data, read);
				file.close();
				result = new DataChunk(fileId, chunkNo, body, read);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return result;
	}

	public boolean decrementDegree(String origin) {
		return origins.remove(origin);
	}
}
