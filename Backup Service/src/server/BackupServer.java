package server;

import java.io.File;

import server.protocol.ChunkBackupProtocolInitiator;
import server.protocol.MDBListener;
import ui.BackupListener;

public class BackupServer {

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

		record.printChunksHeld();
		files.printFilesHeld();
		updateVisuals();
		(new Thread(new MDBListener(this))).start();
	}

	public boolean backupFile(File file, int degree) {

		ChunkedFile chunkedFile = new ChunkedFile();
		if (!chunkedFile.loadFile(file))
			return false;

		ChunkBackupProtocolInitiator backup = new ChunkBackupProtocolInitiator(
				chunkedFile, degree);
		new Thread(backup).start();

		files.addFile(new FileInfo(chunkedFile.fileName, chunkedFile.path,
				chunkedFile.size, 0, degree));

		updateVisuals();

		return true;

	}

	public void updateVisuals() {
		if (listener != null)
			listener.updateChunks(record.getChunks());
		if (listener != null)
			listener.updateFiles(files.getFiles());
	}

	public ChunksRecord getRecord() {
		return record;

	}

	public void setListener(BackupListener listener) {
		this.listener = listener;

	}

	public void deleteData() {
		record.deleteData();
		if (listener != null)
			listener.updateChunks(record.getChunks());

	}

	public FilesRecord getFilesRecord() {
		return files;
	}

}
