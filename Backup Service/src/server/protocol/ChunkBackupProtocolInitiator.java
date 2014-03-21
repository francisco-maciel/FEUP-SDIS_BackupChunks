package server.protocol;

import java.util.Vector;

import server.ChunkedFile;

public class ChunkBackupProtocolInitiator implements Runnable {
	ChunkedFile file;
	int desiredDegree;

	public ChunkBackupProtocolInitiator(ChunkedFile file, int degree) {
		this.file = file;
		desiredDegree = degree;
	}

	@Override
	public void run() {
		Vector<Thread> chunkThreads = new Vector<Thread>();

		for (int i = 0; i < file.getNChunks(); i++) {
			Thread nT = new BackupChunk(file.getChunk(i), desiredDegree);
			chunkThreads.add(nT);
			nT.start();
		}

		for (Thread t : chunkThreads)
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		System.out.println("FILE BACKUP COMPELTE");
	}
}
