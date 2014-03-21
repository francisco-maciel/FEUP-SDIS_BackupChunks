package server;

import java.io.File;

import ui.BackupListener;

public class BackupServer {

	String mc_address, mdb_address, mdr_address;
	int mc_port, mdb_port, mdr_port;

	ChunksRecord record;
	private BackupListener listener;
	private FilesRecord files;

	public BackupServer(String mc_address, int mc_port, String mdb_address,
			int mdb_port, String mdr_address, int mdr_port) {

		this.mc_address = mc_address;
		this.mdb_address = mdb_address;
		this.mdr_address = mdr_address;
		this.mc_port = mc_port;
		this.mdb_port = mdb_port;
		this.mdr_port = mdr_port;
		this.record = ChunksRecord.getChunksRecord();
		this.files = FilesRecord.getFilesRecord();
		listener = null;

	}

	public void start() {

		record.printChunksHeld();
		files.printFilesHeld();
		updateVisuals();

	}

	public boolean backupFile(File file, int degree) {

		ChunkedFile chunkedFile = new ChunkedFile();
		if (chunkedFile.loadFile(file))
			System.out.println(chunkedFile);
		else
			return false;

		for (int i = 0; i < chunkedFile.data.size(); i++) {
			DataChunk dc = chunkedFile.getChunk(i);

			record.addChunk(dc);
		}

		files.addFile(new FileInfo(chunkedFile.fileName, chunkedFile.path,
				chunkedFile.size, 0, degree));
		updateVisuals();

		return true;

	}

	private void updateVisuals() {
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
