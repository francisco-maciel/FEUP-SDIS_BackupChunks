package server;

import java.io.Serializable;

public class FileInfo implements Serializable {

	private static final long serialVersionUID = -4106048689272352962L;
	String name;
	String path;
	int size;
	int desiredDegree;

	// TODO data modificacao
	public FileInfo(String name, String path, int size, int desiredDegree) {
		this.name = name;
		this.size = size;
		this.path = path;
		this.desiredDegree = desiredDegree;
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

	public void setDesiredDegree(int deg) {
		this.desiredDegree = deg;
	}

	public int getDesiredDegree() {
		return desiredDegree;
	}

	public String toString() {
		return name;

	}
}
