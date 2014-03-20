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
		if (listener != null)
			listener.updateChunks(record.getChunks());

	}

	public boolean backupFile(File file) {

		ChunkedFile chunkedFile = new ChunkedFile();
		if (chunkedFile.loadFile(file))
			System.out.println(chunkedFile);
		else
			System.out.println("File not found");

		for (int i = 0; i < chunkedFile.data.size(); i++) {
			DataChunk dc = chunkedFile.getChunk(i);

			record.addChunk(dc);
		}
		if (listener != null)
			listener.updateChunks(record.getChunks());

		return true;

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

}
