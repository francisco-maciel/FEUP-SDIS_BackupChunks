package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Vector;

import utils.Debug;

public class ChunksRecord {

	public static final String RECORD_NAME = ".chunkRecord";
	private static ChunksRecord chuncksRecord;

	private Vector<Chunk> chunks;
	private Integer max_size;
	private int total_size;
	Vector<String> deletedFiles;

	@SuppressWarnings("unchecked")
	private ChunksRecord() {

		createDataFolder();

		File f = new File("data" + File.separator + RECORD_NAME);
		if (f.exists() && !f.isDirectory()) {
			try {
				InputStream file = new FileInputStream(f);
				InputStream buffer = new BufferedInputStream(file);
				ObjectInput input = new ObjectInputStream(buffer);

				chunks = (Vector<Chunk>) input.readObject();
				total_size = countTotalSize();
				max_size = (Integer) input.readObject();
				deletedFiles = (Vector<String>) input.readObject();
				input.close();

			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}

		} else {
			chunks = new Vector<Chunk>();
			deletedFiles = new Vector<String>();
			total_size = 0;
			max_size = new Integer(10 * 1024 * 1024);
			Debug.debug("Created original chunk Records File");
			updateRecordFile();
		}

	}

	private int countTotalSize() {
		int total = 0;
		for (Chunk c : chunks) {
			total += c.getSize();
		}
		return total;
	}

	private void createDataFolder() {

		File theDir = new File("data");
		if (!theDir.exists())
			theDir.mkdir();
		File theDir2 = new File("data" + File.separator + "chunks");
		if (!theDir2.exists())
			theDir2.mkdir();

	}

	public static ChunksRecord get() {
		if (chuncksRecord == null) {
			chuncksRecord = new ChunksRecord();
		}
		return chuncksRecord;
	}

	public int getNumberChunks() {
		return chunks.size();
	}

	public void printChunksHeld() {
		int n = getNumberChunks();
		System.out.println("Chunks held: " + n);

		for (int i = 0; i < n; i++) {
			System.out.println(chunks.get(i));
		}
	}

	public synchronized boolean addChunk(DataChunk newC) {
		// TODO if chunk does not exists and ERROR throw exeption. if chunk
		// exists just return false
		// update behaviour of MDBputchunk listener accordingly
		File f = new File("data" + File.separator + "chunks" + File.separator
				+ newC.getChunkFileName());
		try {
			if (!f.createNewFile())
				return false;

			OutputStream file = new FileOutputStream(f);
			BufferedOutputStream buffer = new BufferedOutputStream(file);

			buffer.write(newC.getData(), 0, newC.getSize());
			buffer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		Chunk c = new Chunk(newC.getName(), newC.getNo(), newC.getSize());
		c.setOrigins(newC.getOrigins());
		c.desiredDegree = newC.desiredDegree;

		chunks.add(c);
		total_size = countTotalSize();

		updateRecordFile();
		return true;
	}

	public void deleteData() {
		deleteDirectory(new File("data" + File.separator + "chunks"));
		createDataFolder();
		chunks.removeAllElements();
		total_size = countTotalSize();
		updateRecordFile();
	}

	public synchronized boolean deleteChunk(Chunk newC) {
		try {
			File f = new File("data" + File.separator + "chunks"
					+ File.separator + newC.getChunkFileName());
			return f.delete();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public synchronized boolean deleteChunksOfFile(String fileId) {

		try {
			for (int i = 0; i < chunks.size(); i++) {
				if (chunks.get(i).getName().equals(fileId)) {
					// DELETE CHUNK
					deleteChunk(chunks.get(i));
					chunks.remove(i);

					i--;
				}
			}
			total_size = countTotalSize();
			deletedFiles.add(fileId);
			updateRecordFile();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public static boolean deleteDirectory(File directory) {
		// Codigo retirado de
		// http://stackoverflow.com/questions/3775694/deleting-folder-from-java
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (null != files) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						deleteDirectory(files[i]);
					} else {
						files[i].delete();
					}
				}
			}
		}
		return (directory.delete());
	}

	private void updateRecordFile() {
		try {
			File f = new File("data" + File.separator + RECORD_NAME);
			f.createNewFile();
			OutputStream file = new FileOutputStream(f);
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			output.writeObject(chunks);
			output.writeObject(max_size);
			output.writeObject(deletedFiles);
			output.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized Vector<Chunk> getChunks() {
		return chunks;
	}

	public synchronized boolean incrementChunkValue(String fileId, String origin, int chunkNo) {
		try {
			int index = getChunkIndex(fileId, chunkNo);
			if (index != -1) {

				if (chunks.get(index).incrementDegree(origin)) {
					updateRecordFile();
					return true;
				}
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public synchronized int getChunkIndex(String fileId, int chunkNo) {
		for (int i = 0; i < chunks.size(); i++) {
			if (chunks.get(i).chunkNo == chunkNo
					&& chunks.get(i).getName().equals(fileId))
				return i;
		}

		return -1;
	}

	public int getMaxSize() {
		return max_size;
	}

	public int getTotalSize() {
		return total_size;
	}

	public void setMaxSize(int ans) {
		this.max_size = ans;
		updateRecordFile();
	}

	public boolean wasDeleted(String name) {
		return deletedFiles.contains((String) name);
	}

}