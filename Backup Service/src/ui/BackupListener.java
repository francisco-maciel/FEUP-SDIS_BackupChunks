package ui;

import java.util.Vector;

import server.Chunk;
import server.FileInfo;

public interface BackupListener {
	public void updateChunks(Vector<Chunk> chunks);

	public void updateFiles(Vector<FileInfo> chunks);

	public void updateProgressBar(int value);
}
