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
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import server.protocol.RemovedSender;
import ui.BackupListener;
import utils.Debug;

public class ChunksRecord {

	public static final String RECORD_NAME = ".chunkRecord";
	private static ChunksRecord chuncksRecord;

	private Vector<Chunk> chunks;
	private Vector<Chunk> remove_list;
	private Integer max_size;
	private int total_size;
	Vector<String> deletedFiles;

	@SuppressWarnings("unchecked")
	private ChunksRecord() {

		createDataFolder();
		remove_list = new Vector<Chunk>();
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

	public void updateTotalSize() {
		total_size = countTotalSize();
	}

	public int countTotalSize() {
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

	public synchronized boolean addChunk(DataChunk newC, BackupListener l) {

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
		deletedFiles.remove(c.getName());

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

		boolean found = false;
		try {
			for (int i = 0; i < chunks.size(); i++) {
				if (chunks.get(i).getName().equals(fileId)) {
					// DELETE CHUNK
					deleteChunk(chunks.get(i));
					chunks.remove(i);
					found = true;
					i--;
				}
			}
			total_size = countTotalSize();
			deletedFiles.add(fileId);
			updateRecordFile();
			return found;
		} catch (Exception e) {
			e.printStackTrace();
			return found;
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

	public void updateRecordFile() {
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

	public Vector<Chunk> getChunks() {
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

	public int getChunkIndex(String fileId, int chunkNo) {
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

	public void setMaxSize(int ans, BackupListener l) {
		this.max_size = ans;
		updateRecordFile();
		if (max_size < total_size)
			spaceReclaim(total_size - max_size, l, false);
	}

	private boolean spaceReclaim(int spaceNeeded, BackupListener listener, boolean sync) {

		// puts ideal chunks to remove on start of array
		orderChunksByRemoveOrder();
		boolean returnv = true;
		int space_removed = 0;
		int i = 0;
		Vector<Chunk> chunks_to_remove = new Vector<Chunk>();

		while (space_removed < spaceNeeded) {
			space_removed += chunks.get(i).getSize();
			chunks_to_remove.add(chunks.get(i));
			i++;
		}

		remove_list.addAll(chunks_to_remove);

		int x = 0;
		for (Chunk c : chunks_to_remove) {
			x++;
			if (x % 5 == 0)
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			AtomicBoolean result = new AtomicBoolean(true);

			if (sync) {

				Thread t = new RemovedSender(c.getName(), c.getNo(), result);
				t.start();
				try {

					t.join();

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				returnv = returnv && result.get();
				listener.updateChunks(ChunksRecord.get().getChunks());

			} else {
				(new RemovedSender(c.getName(), c.getNo(), result)).start();
				listener.updateChunks(ChunksRecord.get().getChunks());
			}

		}
		return returnv;

	}

	private void orderChunksByRemoveOrder() {
		Collections.sort(chunks, new Comparator<Chunk>() {
			@Override
			public int compare(Chunk o2, Chunk o1) {

				int deg_comp = (new Integer(o1.getActualDegree()
						- o1.desiredDegree)).compareTo(new Integer((o2
						.getActualDegree() - o2.desiredDegree)));
				if (deg_comp != 0)
					return deg_comp;
				else
					return (new Integer(o1.getSize())).compareTo(new Integer(
							(o2.getSize())));

			}

		});
	}

	public boolean wasDeleted(String name) {
		return deletedFiles.contains((String) name);
	}

	public void setNotDeleted(String cryptedName) {
		deletedFiles.remove(cryptedName);
	}

	public boolean onRemovedList(String fileId, int chunkNo) {
		for (Chunk c : remove_list) {
			if (c.getName().equals(fileId) && chunkNo == c.getNo()) {
				return true;
			}
		}
		return false;
	}

	public void removeFromRemoveList(String name, int no) {
		for (int i = 0; i < remove_list.size(); i++) {
			if (remove_list.get(i).getName().equals(name)
					&& remove_list.get(i).getNo() == no)
				remove_list.remove(i);

		}
	}

	public boolean decrementChunkValue(String fileId, String origin, int chunkNo) {
		try {
			int index = getChunkIndex(fileId, chunkNo);
			if (index != -1) {

				if (chunks.get(index).decrementDegree(origin)) {
					updateRecordFile();
					if (chunks.get(index).getActualDegree() < chunks.get(index).desiredDegree)
						return true;
					else
						return false;
				}
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public void removeFromChunks(String name, int no) {
		for (int i = 0; i < chunks.size(); i++) {
			if (chunks.get(i).getName().equals(name)
					&& chunks.get(i).getNo() == no)
				chunks.remove(i);

		}
	}
}