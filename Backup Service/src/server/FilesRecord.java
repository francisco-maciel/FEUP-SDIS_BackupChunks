package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Vector;

public class FilesRecord {

	public static final String RECORD_NAME = ".filesRecord";
	private static FilesRecord fileRecord;

	private Vector<FileInfo> files;

	@SuppressWarnings("unchecked")
	private FilesRecord() {

		createDataFolder();

		File f = new File("data" + File.separator + RECORD_NAME);
		if (f.exists() && !f.isDirectory()) {
			try {
				InputStream file = new FileInputStream(f);
				InputStream buffer = new BufferedInputStream(file);
				ObjectInput input = new ObjectInputStream(buffer);

				files = (Vector<FileInfo>) input.readObject();
				input.close();

			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}

		} else {
			files = new Vector<FileInfo>();
			updateRecordFile();
		}

	}

	private void createDataFolder() {

		File theDir = new File("data");
		if (!theDir.exists())
			theDir.mkdir();
	}

	public static FilesRecord getFilesRecord() {
		if (fileRecord == null) {
			fileRecord = new FilesRecord();
		}
		return fileRecord;
	}

	public int getNumberFiles() {
		return files.size();
	}

	public void printFilesHeld() {
		int n = getNumberFiles();
		System.out.println("Files virtually backed: " + n);

		for (int i = 0; i < n; i++) {
			System.out.println(files.get(i));
		}
	}

	public boolean addFile(FileInfo dc) {
		return addFile(dc.getName(), dc.getPath(), dc.getSize(),
				dc.getReplicationDegree(), dc.getDesiredDegree());
	}

	public boolean addFile(String fileName, String path, int size, int replicationDegree, int desiredDegree) {
		FileInfo newFile = new FileInfo(fileName, path, size,
				replicationDegree, desiredDegree);

		files.add(newFile);

		updateRecordFile();
		return true;
	}

	private void updateRecordFile() {
		try {
			File f = new File("data" + File.separator + RECORD_NAME);
			f.createNewFile();
			OutputStream file = new FileOutputStream(f);
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			output.writeObject(files);
			output.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Vector<FileInfo> getFiles() {
		return files;
	}
}