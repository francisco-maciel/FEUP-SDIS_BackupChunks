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

	public static FilesRecord get() {
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
				dc.getDesiredDegree(), dc.cryptedName);
	}

	public boolean addFile(String fileName, String path, int size, int desiredDegree, String crypt) {
		FileInfo newFile = new FileInfo(fileName, path, size, desiredDegree,
				crypt);

		boolean found = false;

		for (FileInfo f : files) {
			if (f.getName().equals(newFile.getName())) {
				found = true;
				f.setDesiredDegree(desiredDegree);
				f.setPath(path);
				f.setSize(size);
			}
		}
		if (!found)
			files.add(newFile);

		updateRecordFile();
		return true;
	}

	public boolean deleteFile(FileInfo dc) {
		return deleteFile(dc.getName());
	}

	public boolean deleteFile(String fileName) {

		boolean found = false;
		FileInfo newFile = null;
		for (FileInfo f : files) {
			if (f.getName().equals(fileName)) {
				found = true;
				newFile = f;
			}
		}
		if (found)
			files.remove(newFile);

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

	public FileInfo getFileInfo(String selectedNode) {
		for (FileInfo f : files) {
			if (f.getName().equals(selectedNode))
				return f;
		}
		return null;
	}
}