package server;

import java.io.Serializable;

public class FileInfo implements Serializable {

	private static final long serialVersionUID = -4106048689272352962L;
	String name;
	String path;
	int size;
	int desiredDegree;
	public String cryptedName;

	public FileInfo(String name, String path, int size, int desiredDegree,
			String crypt) {
		this.name = name;
		this.size = size;
		this.path = path;
		this.desiredDegree = desiredDegree;
		this.cryptedName = crypt;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	public int getNumChunks() {
		int n = (int) Math.ceil(size / 64000.0);

		if (size % 64000 == 0)
			n += 1;
		return n;
	}

	public void setDesiredDegree(int deg) {
		this.desiredDegree = deg;
	}

	public int getDesiredDegree() {
		return desiredDegree;
	}

	public String toString() {
		return name;

	}

	public String getCryptedName() {
		return cryptedName;
	}
}
