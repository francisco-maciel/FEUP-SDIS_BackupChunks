package server.protocol;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JOptionPane;

import server.ChunkedFile;
import server.FileInfo;
import server.FilesRecord;

public class ChunkBackupProtocolInitiator implements Runnable {
	ChunkedFile file;
	int desiredDegree;

	public ChunkBackupProtocolInitiator(ChunkedFile file, int degree) {
		this.file = file;
		desiredDegree = degree;
	}

	@Override
	public void run() {
		boolean result = true;
		for (int i = 0; i < file.getNChunks(); i += 3) {
			AtomicBoolean resultA = new AtomicBoolean(true);
			AtomicBoolean resultB = new AtomicBoolean(true);
			AtomicBoolean resultC = new AtomicBoolean(true);
			BackupChunk tA, tB = null, tC = null;

			// THREAD A
			tA = new BackupChunk(file.getChunk(i), desiredDegree, resultA);
			tA.start();

			// THREAD B
			if (i + 1 < file.getNChunks()) {
				tB = new BackupChunk(file.getChunk(i + 1), desiredDegree,
						resultB);
				tB.start();
			}

			// THREAD C
			if (i + 2 < file.getNChunks()) {
				tC = new BackupChunk(file.getChunk(i + 2), desiredDegree,
						resultC);
				tC.start();
			}

			try {
				tA.join();
				if (tB != null)
					tB.join();
				if (tC != null)
					tC.join();

				if (!resultA.get() || !resultB.get() || !resultC.get()) {
					result = false;
					break;
				}
				System.out.println(resultA.toString() + resultB.toString()
						+ resultC.toString());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		if (result) {
			FilesRecord.getFilesRecord().addFile(
					new FileInfo(file.fileName, file.path, file.size,
							desiredDegree));
			System.out.println("FILE BACKUP COMPELTE");
		} else {
			// TODO remove chunks already backed
			JOptionPane.showMessageDialog(null, "The file " + file.fileName
					+ " failed to backup correctly", "File backup failed",
					JOptionPane.WARNING_MESSAGE);

			System.out.println("FILE BACKUP FAILED");

		}
	}
}
