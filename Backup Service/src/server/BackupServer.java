package server;

import java.io.File;
import java.util.Vector;

import server.protocol.ChunkBackupProtocolInitiator;
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
		this.record = ChunksRecord.getChunksRecord();
		this.files = FilesRecord.getFilesRecord();
		listener = null;

	}

	public void start() {

		// record.printChunksHeld();
		// files.printFilesHeld();
		updateVisuals();
		(new Thread(new MDBListener(this))).start();
		(new Thread(new MCListener(this))).start();
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

	public void restoreFile(String chunkName) {
		FileInfo file = null;
		for (int i = 0; i < files.getNumberFiles(); i++) {
			if (files.getFiles().get(i).getName().equals(chunkName))
				file = files.getFiles().get(i);
		}
		ChunkRestoreProtocolInitiator restore = new ChunkRestoreProtocolInitiator(
				file, listener);
		new Thread(restore).start();
		updateVisuals();

	}

	public void deleteFile(String selectedNode) {
		FileInfo file = files.getFileInfo(selectedNode);
		(new DeleteSender(file.getCryptedName())).start();
		files.deleteFile(file);
		updateVisuals();
	}
}
