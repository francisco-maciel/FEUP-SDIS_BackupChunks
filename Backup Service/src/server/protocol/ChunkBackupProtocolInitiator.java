package server.protocol;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JOptionPane;

import server.ChunkedFile;
import server.ChunksRecord;
import server.FileInfo;
import server.FilesRecord;
import server.PutChunkEnhancement;
import ui.BackupListener;

public class ChunkBackupProtocolInitiator extends Thread {
	ChunkedFile file;
	int desiredDegree;
	BackupListener listener;

	public ChunkBackupProtocolInitiator(ChunkedFile file, int degree,
			BackupListener listener) {
		this.file = file;
		desiredDegree = degree;
		this.listener = listener;
	}

	@Override
	public void run() {
		boolean result = true;
		int j = 0;
		listener.updateProgressBar(0);

		for (int i = 0; i < file.getNChunks(); i++)
			PutChunkEnhancement.saveToDisk(file.getChunk(i), desiredDegree);

		FilesRecord.get().addFile(
				new FileInfo(file.fileName, file.path, file.size,
						desiredDegree, file.getCryptName()));

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
					j++;
					result = false;
					break;
				}
				j++;
				listener.updateProgressBar(100 * j / file.getNChunks());
				if (tB != null) {
					j++;
					listener.updateProgressBar(100 * j / file.getNChunks());
				}
				if (tC != null) {
					j++;
					listener.updateProgressBar(100 * j / file.getNChunks());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		if (result) {

			JOptionPane.showMessageDialog(null, "The file " + file.fileName
					+ " backup up successfully", "File backup completed",
					JOptionPane.INFORMATION_MESSAGE);

			System.out.println("FILE BACKUP COMPELTE");
		} else {
			FilesRecord.get().deleteFile(file.fileName);
			(new DeleteSender(file.getCryptName())).start();
			listener.updateProgressBar(0);
			for (int i = 0; i < file.getNChunks(); i++)
				PutChunkEnhancement.destroy(file.getChunk(i));

			JOptionPane.showMessageDialog(null, "The file " + file.fileName
					+ " failed to backup correctly", "File backup failed",
					JOptionPane.WARNING_MESSAGE);
			listener.updateChunks(ChunksRecord.get().getChunks());
			System.out.println("FILE BACKUP FAILED");

		}
		listener.setEnabledButtons(true);

	}
}
