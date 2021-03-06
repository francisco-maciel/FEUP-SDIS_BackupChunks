package server;

import java.io.File;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import server.protocol.CheckAllFiles;
import server.protocol.ChunkBackupProtocolInitiator;
import server.protocol.ChunkEnhancedRestoreProtocolInitiator;
import server.protocol.ChunkRestoreProtocolInitiator;
import server.protocol.DeleteSender;
import server.protocol.MCListener;
import server.protocol.MDBListener;
import ui.BackupListener;

public class BackupServer {

	public static final int DEFAULT_REP_VALUE = 1;
	public static String mc_address, mdb_address, mdr_address;
	public static int mc_port, mdb_port, mdr_port;

	ChunksRecord record;
	private BackupListener listener;
	private FilesRecord files;

	public BackupServer(String mc_address, int mc_port, String mdb_address,
			int mdb_port, String mdr_address, int mdr_port) {

		BackupServer.mc_address = mc_address;
		BackupServer.mdb_address = mdb_address;
		BackupServer.mdr_address = mdr_address;
		BackupServer.mc_port = mc_port;
		BackupServer.mdb_port = mdb_port;
		BackupServer.mdr_port = mdr_port;
		this.record = ChunksRecord.get();
		this.files = FilesRecord.get();
		listener = null;

	}

	public void start() {

		// record.printChunksHeld();
		// files.printFilesHeld();
		(new Thread(new Runnable() {

			@Override
			public void run() {
				PutChunkEnhancement.getAllNonBackedChunks();

			}

		})).start();
		updateVisuals();
		(new MDBListener(this)).start();
		(new MCListener(this)).start();
		(new CheckAllFiles(record.getChunks())).start();
	}

	public boolean backupFile(File file, int degree) {
		listener.setEnabledButtons(false);

		ChunkedFile chunkedFile = new ChunkedFile();
		if (!chunkedFile.loadFile(file))
			return false;

		ChunkBackupProtocolInitiator backup = new ChunkBackupProtocolInitiator(
				chunkedFile, degree, listener);
		new Thread(backup).start();
		updateVisuals();

		return true;

	}

	@SuppressWarnings("unchecked")
	public void updateVisuals() {
		if (listener != null)
			listener.updateChunks((Vector<Chunk>) record.getChunks().clone());
		if (listener != null)
			listener.updateFiles((Vector<FileInfo>) files.getFiles().clone());
	}

	public ChunksRecord getRecord() {
		return record;

	}

	public void setListener(BackupListener listener) {
		this.listener = listener;

	}

	public BackupListener getListener() {
		return listener;
	}

	public void deleteData() {
		record.deleteData();
		if (listener != null)
			listener.updateChunks(record.getChunks());

	}

	public FilesRecord getFilesRecord() {

		return files;
	}

	public void restoreFile(final String chunkName) {
		(new Thread(new Runnable() {

			@Override
			public void run() {

				listener.setEnabledButtons(false);
				FileInfo file = null;
				for (int i = 0; i < files.getNumberFiles(); i++) {
					if (files.getFiles().get(i).getName().equals(chunkName))
						file = files.getFiles().get(i);
				}
				AtomicBoolean result = new AtomicBoolean(false);

				ChunkEnhancedRestoreProtocolInitiator restoreUDP = new ChunkEnhancedRestoreProtocolInitiator(
						file, result, listener);

				restoreUDP.start();

				updateVisuals();

				try {
					restoreUDP.join();

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (result.get() == false) {

					ChunkRestoreProtocolInitiator restore = new ChunkRestoreProtocolInitiator(
							file, listener);
					new Thread(restore).start();
				} else {
				}
				listener.setEnabledButtons(true);

				updateVisuals();

			}

		})).start();

	}

	public void deleteFile(String selectedNode) {
		FileInfo file = files.getFileInfo(selectedNode);
		(new DeleteSender(file.getCryptedName())).start();
		files.deleteFile(file);
		updateVisuals();
	}
}
