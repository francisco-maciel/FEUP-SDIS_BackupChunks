package ui;

import java.util.Vector;

import server.BackedFile;
import server.Chunk;

public interface BackupListener {
	public void updateChunks(Vector<Chunk> chunks);
	public void updateFiles(Vector<BackedFile> chunks);
}
