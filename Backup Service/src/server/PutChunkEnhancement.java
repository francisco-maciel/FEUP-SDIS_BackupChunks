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
import java.util.concurrent.atomic.AtomicBoolean;

import server.protocol.BackupChunk;

public class PutChunkEnhancement {

	public PutChunkEnhancement() {
	}

	public static void saveToDisk(DataChunk dc, int desiredDegree) {
		File theDir = new File("temp");
		if (!theDir.exists())
			theDir.mkdir();

		File save = new File("temp" + File.separator + dc.getChunkFileName());

		try {
			save.createNewFile();

			OutputStream file = new FileOutputStream(save);
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			output.writeObject(dc);
			output.writeObject(new Integer(desiredDegree));
			output.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void getAllNonBackedChunks() {
		File folder = new File("temp");
		if (!folder.exists())
			return;
		else {
			File[] listOfFiles = folder.listFiles();

			Vector<DataChunk> chunks = new Vector<DataChunk>();
			Vector<Integer> degrees = new Vector<Integer>();

			for (int i = 0; i < listOfFiles.length; i++) {
				try {
					InputStream file = new FileInputStream(listOfFiles[i]);
					InputStream buffer = new BufferedInputStream(file);
					ObjectInput input = new ObjectInputStream(buffer);

					chunks.add((DataChunk) input.readObject());
					degrees.add((Integer) input.readObject());
					input.close();

				} catch (IOException | ClassNotFoundException e) {
				}

			}

			for (int i = 0; i < chunks.size(); i += 3) {
				AtomicBoolean resultA = new AtomicBoolean(true);
				AtomicBoolean resultB = new AtomicBoolean(true);
				AtomicBoolean resultC = new AtomicBoolean(true);
				BackupChunk tA, tB = null, tC = null;

				// THREAD A
				tA = new BackupChunk(chunks.get(i), degrees.get(i), resultA);
				tA.start();

				// THREAD B
				if (i + 1 < chunks.size()) {
					tB = new BackupChunk(chunks.get(i + 1), degrees.get(i + 1),
							resultB);
					tB.start();
				}

				// THREAD C
				if (i + 2 < chunks.size()) {
					tC = new BackupChunk(chunks.get(i + 2), degrees.get(i + 2),
							resultC);
					tC.start();
				}

				try {
					tA.join();
					if (tB != null)
						tB.join();
					if (tC != null)
						tC.join();

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
	}

	public static void destroy(DataChunk chunk) {
		File save = new File("temp" + File.separator + chunk.getChunkFileName());
		if (save.exists())
			save.delete();

	}
}
