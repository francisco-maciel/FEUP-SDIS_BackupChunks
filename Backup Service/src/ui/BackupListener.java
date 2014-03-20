package ui;

import java.util.Vector;

import server.FileInfo;
import server.Chunk;

public interface BackupListener {
	public void updateChunks(Vector<Chunk> chunks);

	public void updateFiles(Vector<FileInfo> chunks);
}
